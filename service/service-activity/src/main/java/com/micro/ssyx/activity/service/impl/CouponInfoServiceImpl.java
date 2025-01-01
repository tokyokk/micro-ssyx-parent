package com.micro.ssyx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.micro.ssyx.activity.mapper.CouponInfoMapper;
import com.micro.ssyx.activity.mapper.CouponRangeMapper;
import com.micro.ssyx.activity.mapper.CouponUseMapper;
import com.micro.ssyx.activity.service.CouponInfoService;
import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.enums.CouponRangeType;
import com.micro.ssyx.enums.CouponStatus;
import com.micro.ssyx.model.activity.CouponInfo;
import com.micro.ssyx.model.activity.CouponRange;
import com.micro.ssyx.model.activity.CouponUse;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private CouponUseMapper couponUseMapper;

    @Override
    public IPage<CouponInfo> selectPageCouponInfo(final Page<CouponInfo> pageParam) {
        final Page<CouponInfo> page = new Page<>(pageParam.getCurrent(), pageParam.getSize());
        final Page<CouponInfo> couponInfoPage = baseMapper.selectPage(page, null);

        final List<CouponInfo> couponInfoList = couponInfoPage.getRecords();

        couponInfoList.forEach(couponInfo -> {
            couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
            Optional.ofNullable(couponInfo.getRangeType()).ifPresent(rangeType -> couponInfo.setRangeTypeString(rangeType.getComment()));
        });

        return couponInfoPage;

    }

    @Override
    public CouponInfo getCouponInfo(final String id) {
        final CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    @Override
    public Map<String, Object> findCouponRuleList(final Long id) {
        // 1. 根据优惠卷id查询优惠券基本信息 coupon_info
        final CouponInfo couponInfo = baseMapper.selectById(id);
        // 2. 根据优惠卷id查询coupon_range 查询对应的range_id
        final List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id)
        );
        // couponRangeList获取所有的range_id
        // 2.1 如果range_id的类型是 sku， range_id对应的是sku_info表的id
        // 2.2 如果range_id的类型是 category， range_id对应的是category_info表的id
        final List<Long> rangeIdList = couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());

        final HashMap<String, Object> resultMap = Maps.newHashMap();
        // 3.分别判断进行封装
        if (!CollectionUtils.isEmpty(rangeIdList)) {
            // 3.1如果规则类型是sku，得到skuId，远程调用根据多个skuId查询sku信息
            if (Objects.equals(couponInfo.getRangeType(), CouponRangeType.SKU)) {
                // 远程调用根据多个skuId查询sku信息
                final List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(rangeIdList);
                resultMap.put("skuInfoList", skuInfoList);
            } else if (Objects.equals(couponInfo.getRangeType(), CouponRangeType.CATEGORY)) {
                // 3.2如果规则类型是category，得到categoryId，远程调用根据多个categoryId查询category信息
                // 远程调用根据多个categoryId查询category信息
                final List<Category> categoryList = productFeignClient.findCategoryList(rangeIdList);
                resultMap.put("categoryList", categoryList);
            }

        }
        return resultMap;
    }

    @Override
    public Boolean saveCouponRule(final CouponRuleVo couponRuleVo) {
        // 1。根据优惠卷id删除优惠券规则
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponRuleVo.getCouponId()));
        // 2.更新优惠卷基本信息
        final CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());
        baseMapper.updateById(couponInfo);

        // 3.添加新的优惠券规则数据
        final List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        couponRangeList.forEach(couponRange -> {
            couponRange.setCouponId(couponRuleVo.getCouponId());
            couponRangeMapper.insert(couponRange);
        });
        return Boolean.TRUE;
    }

    @Override
    public List<CouponInfo> findCouponInfoList(final Long skuId, final Long userId) {
        final SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);

        return baseMapper.selectCouponInfoList(skuInfo.getId(), skuInfo.getCategoryId(), userId);
    }

    @Override
    public List<CouponInfo> findCartCouponInfo(final List<CartInfo> cartInfoList, final Long userId) {
        // 1 根据userId获取用户优惠券列表 coupon_use coupon_info
        final List<CouponInfo> userCouponInfoList = baseMapper.selectCartCouponInfoList(userId);

        if (CollectionUtils.isEmpty(userCouponInfoList)) {
            return new ArrayList<>();
        }

        // 2 获取所有优惠卷id列表
        final List<Long> couponIdList = userCouponInfoList.stream().map(CouponInfo::getId).collect(Collectors.toList());

        // 3 查询优惠卷对应的范围 coupon_range
        final List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                Wrappers.lambdaQuery(CouponRange.class)
                        .in(CouponRange::getCouponId, couponIdList)
        );

        // 4 获取优惠卷id对应skuId列表！优惠卷id进行分组得到map
        final Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);

        // 5 遍历全部优惠卷集合，判断优惠卷类型，全场通用、sku和分类
        // 优惠后减少金额
        BigDecimal reduceAmount = new BigDecimal(0);
        // 记录最优优惠券
        CouponInfo optimalCouponInfo = null;
        for (final CouponInfo couponInfo : userCouponInfoList) {
            if (couponInfo.getRangeType() == CouponRangeType.ALL) {
                // 全场通用
                // 判断是否满足优惠使用门槛
                // 计算购物车商品的总价
                final BigDecimal totalAmount = computeTotalAmount(cartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1);
                }
            } else {
                // 优惠券id对应的满足使用范围的购物项skuId列表
                final List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                // 当前满足使用范围的购物项
                final List<CartInfo> currentCartInfoList = cartInfoList.stream().filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                final BigDecimal totalAmount = computeTotalAmount(currentCartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1);
                }
            }
            if (couponInfo.getIsSelect() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }
            // 返回List<CouponInfo>
            if (null != optimalCouponInfo) {
                optimalCouponInfo.setIsOptimal(1);
            }
        }

        return userCouponInfoList;
    }

    @Override
    public CouponInfo findRangeSkuIdList(final List<CartInfo> cartInfoList, final Long couponId) {
        final CouponInfo couponInfo = baseMapper.selectById(couponId);
        if (Objects.isNull(couponInfo)) {
            return null;
        }

        final List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                Wrappers.lambdaQuery(CouponRange.class)
                        .eq(CouponRange::getCouponId, couponId)
        );
        final Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        final List<Long> skuIdList = couponIdToSkuIdMap.entrySet().iterator().next().getValue();
        couponInfo.setSkuIdList(skuIdList);
        return couponInfo;
    }

    @Override
    public Boolean updateCouponInfoUseStatus(final Long couponId, final Long userId, final Long orderId) {

        final CouponUse couponUse = couponUseMapper.selectOne(
                Wrappers.lambdaQuery(CouponUse.class)
                        .eq(CouponUse::getCouponId, couponId)
                        .eq(CouponUse::getUserId, userId)
                        .eq(CouponUse::getOrderId, orderId)
        );
        couponUse.setCouponStatus(CouponStatus.USED);

        couponUseMapper.updateById(couponUse);

        return Boolean.TRUE;
    }

    /**
     * 根据购物车信息和优惠券范围列表，构建优惠券ID到SKU ID列表的映射。
     * 该映射用于确定哪些SKU适用于特定优惠券。
     *
     * @param cartInfoList    购物车信息列表，包含SKU和类别ID。
     * @param couponRangeList 优惠券范围列表，定义了优惠券适用的SKU或类别。
     * @return 返回一个映射，其中键是优惠券ID，值是适用于该优惠券的SKU ID列表。
     */
    private Map<Long, List<Long>> findCouponIdToSkuIdMap(final List<CartInfo> cartInfoList, final List<CouponRange> couponRangeList) {
        final Map<Long, List<Long>> couponIdToSkuIdMap = Maps.newHashMap();

        // 根据优惠卷id进行分组
        final Map<Long, List<CouponRange>> couponRangeToRangeListMap = couponRangeList.stream().collect(Collectors.groupingBy(CouponRange::getCouponId));

        couponRangeToRangeListMap.forEach((couponId, rangeList) -> {
            final Set<Long> skuIdSet = new HashSet<>();
            cartInfoList.forEach(cartInfo -> {
                rangeList.forEach(couponRange -> {
                    if (couponRange.getRangeType() == CouponRangeType.SKU
                            && couponRange.getRangeId().longValue() == cartInfo.getSkuId().longValue()) {
                        // 如果是sku类型，skuIdList包含skuId
                        skuIdSet.add(cartInfo.getSkuId());
                    } else if (couponRange.getRangeType() == CouponRangeType.CATEGORY
                            && couponRange.getRangeId().longValue() == cartInfo.getCategoryId().longValue()) {
                        // 如果是category类型，skuIdList包含skuId
                        skuIdSet.add(cartInfo.getSkuId());
                    }
                });
            });
            couponIdToSkuIdMap.put(couponId, new ArrayList<>(skuIdSet));
        });

        return couponIdToSkuIdMap;
    }

    private BigDecimal computeTotalAmount(final List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (final CartInfo cartInfo : cartInfoList) {
            final BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }
}
