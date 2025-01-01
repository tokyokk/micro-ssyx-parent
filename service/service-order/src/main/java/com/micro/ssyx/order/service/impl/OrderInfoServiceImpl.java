package com.micro.ssyx.order.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.micro.ssyx.activity.client.ActivityFeignClient;
import com.micro.ssyx.client.cart.CartFeignClient;
import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.client.user.UserFeignClient;
import com.micro.ssyx.common.auth.AuthContextHolder;
import com.micro.ssyx.common.constant.RedisConst;
import com.micro.ssyx.common.exception.SsyxException;
import com.micro.ssyx.common.result.ResultCodeEnum;
import com.micro.ssyx.common.utils.DateUtil;
import com.micro.ssyx.enums.*;
import com.micro.ssyx.model.activity.ActivityRule;
import com.micro.ssyx.model.activity.CouponInfo;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.model.order.OrderInfo;
import com.micro.ssyx.model.order.OrderItem;
import com.micro.ssyx.mq.constant.MQConst;
import com.micro.ssyx.mq.service.RabbitService;
import com.micro.ssyx.order.mapper.OrderInfoMapper;
import com.micro.ssyx.order.mapper.OrderItemMapper;
import com.micro.ssyx.order.service.OrderInfoService;
import com.micro.ssyx.vo.order.CartInfoVo;
import com.micro.ssyx.vo.order.OrderConfirmVo;
import com.micro.ssyx.vo.order.OrderSubmitVo;
import com.micro.ssyx.vo.order.OrderUserQueryVo;
import com.micro.ssyx.vo.product.SkuStockLockVo;
import com.micro.ssyx.vo.user.LeaderAddressVo;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-07-08
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CartFeignClient cartFeignClient;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private ActivityFeignClient activityFeignClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RabbitService rabbitService;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderConfirmVo confirmOrder() {
        final Long userId = AuthContextHolder.getUserId();
        // 获取用户对应的团长信息
        final LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);

        // 获取购物车里面选中的商品
        final List<CartInfo> cartInfoList = cartFeignClient.getCartCheckedList(userId);

        // 唯一订单号生成
        final String orderNo = System.currentTimeMillis() + "";
        redisTemplate.opsForValue().set(RedisConst.ORDER_REPEAT + orderNo, orderNo, 24,
                TimeUnit.HOURS);

        // 获取购物车中满足条件和优惠卷信息
        final OrderConfirmVo orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);

        orderConfirmVo.setLeaderAddressVo(leaderAddressVo);
        orderConfirmVo.setOrderNo(orderNo);

        return orderConfirmVo;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Long submitOrder(final OrderSubmitVo orderParamVo) {
        // 1 设置给哪个用户生成订单，设置orderParamVo的userId
        final Long userId = AuthContextHolder.getUserId();
        orderParamVo.setUserId(userId);

        // 2 订单不能重复提交，重复提交验证
        final String orderNo = orderParamVo.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            throw new SsyxException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        final String script = "if(redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";
        final Boolean flag = redisTemplate.execute(
                new DefaultRedisScript<>(script, Boolean.class),
                Collections.singletonList(RedisConst.ORDER_REPEAT + orderNo),
                orderNo
        );

        if (Boolean.FALSE.equals(flag)) {
            throw new SsyxException(ResultCodeEnum.REPEAT_SUBMIT);
        }

        // 3 验证库存，锁定库存
        // 3.1远程调用service-cart，获取购物车中选中的商品
        final List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);

        // 3.2 购物车中有很多商品，商品不同类型，重点处理普通类型商品
        final List<CartInfo> commonSkuList = cartCheckedList.stream()
                .filter(cartInfo -> Objects.equals(cartInfo.getSkuType(), SkuType.COMMON.getCode()))
                .collect(Collectors.toList());

        // 3.3 把获取购物车里面普通华山派List集合，List<CartInfo> 转换List<SkuStockLockVo>
        if (!CollectionUtils.isEmpty(commonSkuList)) {
            final List<SkuStockLockVo> commonSkuStockLockVoList = commonSkuList.stream().map(common -> {
                final SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                skuStockLockVo.setSkuId(common.getSkuId());
                skuStockLockVo.setSkuNum(common.getSkuNum());
                return skuStockLockVo;
            }).collect(Collectors.toList());

            // 3.4 远程调用service-product模块实现商品锁定：验证库存并锁定库存也要保证其原执行
            final Boolean isLockSuccess = productFeignClient.checkAndLock(commonSkuStockLockVoList, orderNo);
            if (!isLockSuccess) {
                throw new SsyxException(ResultCodeEnum.ORDER_STOCK_FALL);
            }
        }

        // 4 下单
        final Long orderId = this.saveOrder(orderParamVo, cartCheckedList);

        // 下单完成删除购物车中到记录
        rabbitService.sendMessage(MQConst.EXCHANGE_ORDER_DIRECT, MQConst.ROUTING_DELETE_CART, orderParamVo.getUserId());

        // 5 返回订单id
        return orderId;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Long saveOrder(final OrderSubmitVo orderParamVo, final List<CartInfo> cartInfoList) {
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }

        final Long userId = AuthContextHolder.getUserId();
        // 查询用户提货点以及团长信息
        final LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        if (Objects.isNull(leaderAddressVo)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }

        // 计算金额，营销活动和优惠卷金额
        // 营销活动金额
        final Map<String, BigDecimal> activitySplitAmountMap = this.computeActivitySplitAmount(cartInfoList);

        // 优惠卷金额
        final Map<String, BigDecimal> couponInfoSplitAmountMap = this.computeCouponInfoSplitAmount(cartInfoList,
                orderParamVo.getCouponId());

        // 封装订单项数据
        final List<OrderItem> orderItemList = new ArrayList<>();

        cartInfoList.forEach(cartInfo -> {
            final OrderItem orderItem = new OrderItem();
            orderItem.setId(null);
            orderItem.setCategoryId(cartInfo.getCategoryId());
            if (Objects.equals(cartInfo.getSkuType(), SkuType.COMMON.getCode())) {
                orderItem.setSkuType(SkuType.COMMON);
            } else {
                orderItem.setSkuType(SkuType.SECKILL);
            }
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setImgUrl(cartInfo.getImgUrl());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setLeaderId(orderParamVo.getLeaderId());

            // 营销活动金额
            BigDecimal activityAmount = activitySplitAmountMap.get("activity:" + orderItem.getSkuId());

            if (Objects.nonNull(activityAmount)) {
                activityAmount = new BigDecimal(0);
            }
            orderItem.setSplitActivityAmount(activityAmount);

            // 优惠卷金额
            BigDecimal couponAmount = couponInfoSplitAmountMap.get("coupon:" + orderItem.getSkuId());
            if (Objects.nonNull(couponAmount)) {
                couponAmount = new BigDecimal(0);
            }
            orderItem.setSplitCouponAmount(couponAmount);

            // 总金额
            final BigDecimal skuTotalAmount = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum()));

            // 优惠之后总金额
            final BigDecimal splitTotalAmount = skuTotalAmount.subtract(activityAmount).subtract(couponAmount);
            orderItem.setSplitTotalAmount(splitTotalAmount);
            orderItemList.add(orderItem);
        });

        // 封装OrderInfo信息
        final OrderInfo order = new OrderInfo();
        order.setUserId(userId);
        order.setOrderNo(orderParamVo.getOrderNo());
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setProcessStatus(ProcessStatus.UNPAID);
        order.setCouponId(orderParamVo.getCouponId());
        order.setLeaderId(orderParamVo.getLeaderId());
        order.setLeaderName(leaderAddressVo.getLeaderName());
        order.setLeaderPhone(leaderAddressVo.getLeaderPhone());
        order.setTakeName(leaderAddressVo.getTakeName());
        order.setReceiverName(orderParamVo.getReceiverName());
        order.setReceiverPhone(orderParamVo.getReceiverPhone());
        order.setReceiverProvince(leaderAddressVo.getProvince());
        order.setReceiverCity(leaderAddressVo.getCity());
        order.setReceiverDistrict(leaderAddressVo.getDistrict());
        order.setReceiverAddress(leaderAddressVo.getDetailAddress());
        order.setWareId(cartInfoList.get(0).getWareId());
        order.setProcessStatus(ProcessStatus.UNPAID);

        // 计算订单金额
        final BigDecimal originalTotalAmount = this.computeTotalAmount(cartInfoList);
        BigDecimal activityAmount = activitySplitAmountMap.get("activity:total");
        if (null == activityAmount) {
            activityAmount = new BigDecimal(0);
        }
        BigDecimal couponAmount = couponInfoSplitAmountMap.get("coupon:total");
        if (null == couponAmount) {
            couponAmount = new BigDecimal(0);
        }
        final BigDecimal totalAmount = originalTotalAmount.subtract(activityAmount).subtract(couponAmount);
        // 计算订单金额
        order.setOriginalTotalAmount(originalTotalAmount);
        order.setActivityAmount(activityAmount);
        order.setCouponAmount(couponAmount);
        order.setTotalAmount(totalAmount);

        // 计算团长佣金
        final BigDecimal profitRate = new BigDecimal(0);  // orderSetService.getProfitRate();
        final BigDecimal commissionAmount = order.getTotalAmount().multiply(profitRate);
        order.setCommissionAmount(commissionAmount);

        // 添加信息到订单基本信息表中
        baseMapper.insert(order);

        // 添加订单里面到订单项
        orderItemList.forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        });

        // 如果当前订单使用优惠卷，更新优惠卷状态
        if (order.getCouponId() != null) {
            activityFeignClient.updateCouponInfoUseStatus(order.getCouponId(), userId, order.getId());
        }

        // 下单成功，记录用户购物商品数量，缓存到redis
        final String orderSkuKey = RedisConst.ORDER_SKU_MAP + orderParamVo.getUserId();
        final BoundHashOperations<String, String, Integer> hashOperations = redisTemplate.boundHashOps(orderSkuKey);
        cartInfoList.forEach(cartInfo -> {
            if (Boolean.TRUE.equals(hashOperations.hasKey(cartInfo.getSkuId().toString()))) {
                final Integer orderSkuNum = hashOperations.get(cartInfo.getSkuId().toString()) + cartInfo.getSkuNum();
                hashOperations.put(cartInfo.getSkuId().toString(), orderSkuNum);
            }
        });
        redisTemplate.expire(orderSkuKey, DateUtil.getCurrentExpireTimes(), TimeUnit.SECONDS);

        // 订单id
        return order.getId();

    }

    @Override
    public OrderInfo getOrderInfoById(final Long orderId) {
        if (StringUtils.isEmpty(orderId.toString())) {
            return null;
        }
        final OrderInfo orderInfo = baseMapper.selectById(orderId);

        // 根据orderId查询所有订单享列表
        final List<OrderItem> orderItemList = orderItemMapper.selectList(
                Wrappers.lambdaQuery(OrderItem.class).eq(OrderItem::getOrderId, orderId)
        );
        orderInfo.setOrderItemList(orderItemList);

        return orderInfo;
    }

    @Override
    public OrderInfo getOrderInfoByOrderNo(final String orderNo) {
        return baseMapper.selectOne(
                Wrappers.lambdaQuery(OrderInfo.class).eq(OrderInfo::getOrderNo, orderNo)
        );
    }

    @Override
    public void paySuccess(final String orderNo) {
        // 查询订单状态是否已经修改了支付状态
        final OrderInfo orderInfo = this.getOrderInfoByOrderNo(orderNo);
        if (Objects.isNull(orderInfo) || orderInfo.getOrderStatus().equals(OrderStatus.UNPAID)) {
            return;
        }

        // 更新状态
        this.updateOrderStatus(orderInfo.getId());

        // 扣减库存
        rabbitService.sendMessage(MQConst.EXCHANGE_ORDER_DIRECT, MQConst.ROUTING_MINUS_STOCK, orderNo);
    }

    @Override
    public IPage<OrderInfo> findUserOrderPage(final Page<OrderInfo> pageParam, final OrderUserQueryVo orderUserQueryVo) {
        final Page<OrderInfo> pageModel = baseMapper.selectPage(pageParam,
                Wrappers.lambdaQuery(OrderInfo.class)
                        .eq(OrderInfo::getUserId, orderUserQueryVo.getUserId())
                        .eq(OrderInfo::getOrderStatus, orderUserQueryVo.getOrderStatus())
        );

        final List<OrderInfo> records = pageModel.getRecords();
        records.forEach(orderInfo -> {
            final List<OrderItem> orderItemList = orderItemMapper.selectList(
                    Wrappers.lambdaQuery(OrderItem.class)
                            .eq(OrderItem::getOrderId, orderInfo.getId())
            );
            orderInfo.setOrderItemList(orderItemList);
            // 封装订单状态名称
            orderInfo.getParam().put("orderStatusName", orderInfo.getOrderStatus().getComment());
        });
        return pageModel;
    }

    private void updateOrderStatus(final Long id) {
        final OrderInfo orderInfo = baseMapper.selectById(
                Wrappers.lambdaUpdate(OrderInfo.class)
                        .eq(OrderInfo::getId, id)
        );
        orderInfo.setOrderStatus(OrderStatus.WAITING_DELEVER);
        orderInfo.setProcessStatus(ProcessStatus.WAITING_DELEVER);
        baseMapper.updateById(orderInfo);
    }

    /**
     * 计算总金额
     */
    private BigDecimal computeTotalAmount(final List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (final CartInfo cartInfo : cartInfoList) {
            final BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * 计算购物项分摊的优惠减少金额
     * 打折：按折扣分担
     * 现金：按比例分摊
     *
     * @param cartInfoParamList
     * @return
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(final List<CartInfo> cartInfoParamList) {
        final Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>(16);

        // 促销活动相关信息
        final List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);

        // 活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoVoList)) {
            for (final CartInfoVo cartInfoVo : cartInfoVoList) {
                final ActivityRule activityRule = cartInfoVo.getActivityRule();
                final List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if (null != activityRule) {
                    // 优惠金额， 按比例分摊
                    final BigDecimal reduceAmount = activityRule.getReduceAmount();
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    if (cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:" + cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        // 总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for (final CartInfo cartInfo : cartInfoList) {
                            final BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        // 记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                final CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    final BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    // sku分摊金额
                                    final BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    final BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                final CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    final BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));

                                    // sku分摊金额
                                    final BigDecimal skuDiscountTotalAmount = skuTotalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                                    final BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    final BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    /**
     * 优惠卷优惠金额
     */
    private Map<String, BigDecimal> computeCouponInfoSplitAmount(final List<CartInfo> cartInfoList, final Long couponId) {
        final Map<String, BigDecimal> couponInfoSplitAmountMap = Maps.newHashMap();

        if (null == couponId) return couponInfoSplitAmountMap;
        final CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if (null != couponInfo) {
            // sku对应的订单明细
            final Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>(16);
            for (final CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            // 优惠券对应的skuId列表
            final List<Long> skuIdList = couponInfo.getSkuIdList();
            if (CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            // 优惠券优化总金额
            final BigDecimal reduceAmount = couponInfo.getAmount();
            if (skuIdList.size() == 1) {
                // sku的优化金额
                couponInfoSplitAmountMap.put("coupon:" + skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(), reduceAmount);
            } else {
                // 总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (final Long skuId : skuIdList) {
                    final CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    final BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                // 记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for (int i = 0, len = skuIdList.size(); i < len; i++) {
                        final CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if (i < len - 1) {
                            final BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            // sku分摊金额
                            final BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            final BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());
        }
        return couponInfoSplitAmountMap;
    }
}
