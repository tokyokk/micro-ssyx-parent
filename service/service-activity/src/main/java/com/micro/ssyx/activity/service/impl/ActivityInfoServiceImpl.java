package com.micro.ssyx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.micro.ssyx.activity.mapper.ActivityInfoMapper;
import com.micro.ssyx.activity.mapper.ActivityRuleMapper;
import com.micro.ssyx.activity.mapper.ActivitySkuMapper;
import com.micro.ssyx.activity.service.ActivityInfoService;
import com.micro.ssyx.activity.service.CouponInfoService;
import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.enums.ActivityType;
import com.micro.ssyx.model.activity.ActivityInfo;
import com.micro.ssyx.model.activity.ActivityRule;
import com.micro.ssyx.model.activity.ActivitySku;
import com.micro.ssyx.model.activity.CouponInfo;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.vo.activity.ActivityRuleVo;
import com.micro.ssyx.vo.order.CartInfoVo;
import com.micro.ssyx.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    @Autowired
    private ActivityRuleMapper activityRuleMapper;

    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CouponInfoService couponInfoService;

    @Override
    public IPage<ActivityInfo> selectPage(final Page<ActivityInfo> pageParam) {
        // 分页查询对象里面获取活动列表数据
        final IPage<ActivityInfo> activityInfoPage = baseMapper.selectPage(pageParam, null);
        // 向ActivityInfo中封装活动类型到activityTypeString中去
        activityInfoPage.getRecords().stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });

        return activityInfoPage;
    }

    @Override
    public Map<String, Object> findActivityRuleList(final Long id) {
        final HashMap<String, Object> resultMap = Maps.newHashMap();
        // 1. 根据活动id查询，活动规则列表 activity_rule表
        final LambdaQueryWrapper<ActivityRule> activityRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityRuleLambdaQueryWrapper.eq(ActivityRule::getActivityId, id);
        final List<ActivityRule> activityRuleList = activityRuleMapper.selectList(activityRuleLambdaQueryWrapper);
        resultMap.put("activityRuleList", activityRuleList);

        // 2. 根据活动id查询,查询规则商品skuId列表 activity_sku表
        final List<ActivitySku> activitySkuList = activitySkuMapper.selectList(
                Wrappers.lambdaQuery(ActivitySku.class).eq(ActivitySku::getActivityId, id)
        );
        final List<Long> skuIdList = activitySkuList.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());
        // 2.1 通过远程调用 service-product ，根据skuId列表查询商品信息
        final List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(skuIdList);
        resultMap.put("skuInfoList", skuInfoList);
        return resultMap;
    }

    @Override
    public Boolean saveActivityRule(final ActivityRuleVo activityRuleVo) {
        final Long activityId = activityRuleVo.getActivityId();
        // 1.根据活动id删除之前规则数据
        activityRuleMapper.delete(Wrappers.lambdaQuery(ActivityRule.class).eq(ActivityRule::getActivityId, activityId));
        // 2.删除之前的sku数据
        activitySkuMapper.delete(Wrappers.lambdaQuery(ActivitySku.class).eq(ActivitySku::getActivityId, activityId));


        // 3.保存新的规则数据
        final List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        final ActivityInfo activityInfo = baseMapper.selectById(activityId);
        activityRuleList.forEach(item -> {
            item.setActivityId(activityId);
            item.setActivityType(activityInfo.getActivityType());
            activityRuleMapper.insert(item);
        });

        // 4.保存新的sku数据
        final List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        activitySkuList.forEach(item -> {
            item.setActivityId(activityId);
            activitySkuMapper.insert(item);
        });

        return Boolean.TRUE;
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(final String keyword) {
        // 1. 远程调用 service-product ，根据关键字查询商品信息
        // 2.service-activity调用service-product远程调用得到sku内容信息
        final List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoByKeyword(keyword);
        if (skuInfoList.isEmpty()) {
            return skuInfoList;
        }
        final List<Long> skuIdList = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());

        // 3. 判断商品是否参与活动，如果参与活动，如果之前参与过活动，说明已经在活动中，不需要再次参与
        final List<Long> existSkuIdList = baseMapper.selectSkuIdListExist(skuIdList);

        // 3.1 过滤已经参与活动的商品
        skuInfoList.removeIf(item -> existSkuIdList.contains(item.getId()));

        // 4. 返回商品信息
        return skuInfoList;
    }

    @Override
    public Map<Long, List<String>> findActivity(final List<Long> skuIdList) {
        final HashMap<Long, List<String>> resultMap = new HashMap<>(16);
        skuIdList.forEach(skuId -> {
            final List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);

            if (!CollectionUtils.isEmpty(activityRuleList)) {
                final List<String> ruleList = activityRuleList.stream().map(this::getRuleDesc).collect(Collectors.toList());

                // 处理规则名称，第二种方式
                // activityRuleList.forEach(activityRule -> activityRule.setRuleDesc(getRuleDesc(activityRule)));
                // final List<String> ruleList = activityRuleList.stream().map(ActivityRule::getRuleDesc).collect(Collectors.toList());

                resultMap.put(skuId, ruleList);
            }
        });
        return resultMap;
    }

    @Override
    public Map<String, Object> findActivityAndCoupon(final Long skuId, final Long userId) {
        // 1 根据skuId获取sku营销活动，一个活动有多个规则
        final List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);

        // 2 根据skuId+userId查询优惠卷信息
        final List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

        // 3 封装数据返回
        final HashMap<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("couponInfoList", couponInfoList);
        resultMap.put("activityRuleList", activityRuleList);
        return resultMap;
    }

    @Override
    public List<ActivityRule> findActivityRuleBySkuId(final Long skuId) {
        final List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
        activityRuleList.forEach(activityRule -> activityRule.setRuleDesc(getRuleDesc(activityRule)));
        return activityRuleList;
    }

    @Override
    public OrderConfirmVo findCartActivityAndCoupon(final List<CartInfo> cartInfoList, final Long userId) {
        // 1 获取购物车，每个购物车项参与活动，根据活动规则分组
        // 一个规则对应多个商品
        final List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);

        // 2 计算参与活动之后的金额
        final BigDecimal activityReduceAmount = cartInfoVoList.stream()
                .filter(cartInfoVo -> !StringUtils.isEmpty(cartInfoVo.getActivityRule()))
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3 获取购物车可以使用的优惠卷列表
        final List<CouponInfo> couponInfoList = couponInfoService.findCartCouponInfo(cartInfoList, userId);

        // 4 计算商品使用优惠卷之后的金额，一次只能使用一张优惠卷
        BigDecimal couponReduceAmount = new BigDecimal(0);

        if (!CollectionUtils.isEmpty(couponInfoList)) {
            couponReduceAmount = couponInfoList
                    .stream()
                    .filter(couponInfo -> 1 == couponInfo.getIsOptimal())
                    .map(CouponInfo::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 5 计算没有参加活动，没有使用优惠卷原始金额
        final BigDecimal originalTotalAmount =
                cartInfoList
                        .stream()
                        .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                        .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6 参数活动，使用优惠卷后的金额
        final BigDecimal totalAmount = originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);

        // 7 封装数据返回到CartInfoVo返回
        final OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(cartInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        return orderTradeVo;
    }

    @Override
    public List<CartInfoVo> findCartActivityList(final List<CartInfo> cartInfoList) {
        final List<CartInfoVo> cartInfoVoList = Lists.newArrayList();
        final List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        // 根据所有skuId获取参与活动
        final List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIdList);
        // 根据活动进行分组，每个活动里面有哪些skuId信息
        // map里面的key是分组字段，活动id ，value是每组里面sku列表数据，set集合
        final Map<Long, Set<Long>> activityIdToSkuIdListMap =
                activitySkuList.stream().collect(Collectors.groupingBy(ActivitySku::getActivityId, Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())));

        // 获取活动里面的规则数据，key是活动id，value是活动规则数据
        final Map<Long, List<ActivityRule>> activityIdToActivityRuleListMap;

        final Set<Long> activityIdSet =
                activitySkuList.stream().map(ActivitySku::getActivityId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(activityIdSet)) {
            return Lists.newArrayList();
        }

        final List<ActivityRule> activityRuleList = activityRuleMapper.selectList(
                Wrappers.lambdaQuery(ActivityRule.class)
                        .in(ActivityRule::getActivityId, activityIdSet)
                        .orderByDesc(ActivityRule::getConditionAmount)
                        .orderByDesc(ActivityRule::getConditionNum)
        );

        // 封装数据到activityIdToActivityRuleListMap中，根据活动id进行分组
        activityIdToActivityRuleListMap =
                activityRuleList.stream().collect(Collectors.groupingBy(ActivityRule::getActivityId));

        // 有活动购物项的skuId集合
        final Set<Long> activitySkuIdSet = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(activityIdToSkuIdListMap)) {
            for (final Map.Entry<Long, Set<Long>> entry : activityIdToSkuIdListMap.entrySet()) {
                // 活动id
                final Long activityId = entry.getKey();
                // 获取活动对应的skuId集合
                final Set<Long> currentActivitySkuIdSet = entry.getValue();
                // 获取当前活动对应的购物享列表
                final List<CartInfo> currentActivityCartInfoList =
                        cartInfoList.stream().filter(cartInfo -> currentActivitySkuIdSet.contains(cartInfo.getSkuId())).collect(Collectors.toList());

                // 计算购物享总金额和总数量
                final BigDecimal activityTotalAmount = currentActivityCartInfoList.isEmpty() ? BigDecimal.ZERO : this.computeTotalAmount(currentActivityCartInfoList);
                final int activityTotalNum = currentActivityCartInfoList.isEmpty() ? 0 : this.computeCartNum(currentActivityCartInfoList);

                // 计算活动对应的规则
                // 根据activityId获取活动对应的规则数据
                final List<ActivityRule> currentActivityRuleList = activityIdToActivityRuleListMap.get(activityId);
                final ActivityType activityType =
                        currentActivityRuleList.stream().map(ActivityRule::getActivityType).findFirst().get();
                ActivityRule activityRule = null;
                // 判断活动类型：满减，满量打折
                if (activityType == ActivityType.FULL_REDUCTION) {
                    activityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                } else if (activityType == ActivityType.FULL_DISCOUNT) {
                    activityRule = this.computeFullDiscount(activityTotalNum, activityTotalAmount, currentActivityRuleList);
                }

                // 封装数据到CartInfoVo
                final CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVoList.add(cartInfoVo);

                // 记录哪些购物享参加了活动
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }

        // 没有活动购物项的skuId集合
        // 获取没有参加活动的skuId
        skuIdList.removeAll(activitySkuIdSet);
        if (!CollectionUtils.isEmpty(skuIdList)) {
            final Map<Long, CartInfo> skuIdCartInfoMap = cartInfoList.stream()
                    .collect(Collectors.toMap(CartInfo::getSkuId, Function.identity()));

            skuIdList.forEach(skuId -> {
                final CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setCartInfoList(Collections.singletonList(skuIdCartInfoMap.get(skuId)));
                cartInfoVo.setActivityRule(null);

                cartInfoVoList.add(cartInfoVo);
            });
        }

        return cartInfoVoList;
    }

    /**
     * 计算满量打折最优规则
     *
     * @param totalNum         购买商品总数
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(final Integer totalNum, final BigDecimal totalAmount, final List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        // 该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (final ActivityRule activityRule : activityRuleList) {
            // 如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum >= activityRule.getConditionNum()) {
                final BigDecimal skuDiscountTotalAmount =
                        totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                final BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            // 如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            final StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum - optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            final StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     *
     * @param totalAmount      订单总金额
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(final BigDecimal totalAmount, final List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        // 该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (final ActivityRule activityRule : activityRuleList) {
            // 如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                // 优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            // 如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            final StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            final StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    private BigDecimal computeTotalAmount(final List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (final CartInfo cartInfo : cartInfoList) {
            // 是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                final BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private int computeCartNum(final List<CartInfo> cartInfoList) {
        int total = 0;
        for (final CartInfo cartInfo : cartInfoList) {
            // 是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }

    /**
     * 构造规则名称的方法
     */
    private String getRuleDesc(final ActivityRule activityRule) {
        final ActivityType activityType = activityRule.getActivityType();
        final StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
}
