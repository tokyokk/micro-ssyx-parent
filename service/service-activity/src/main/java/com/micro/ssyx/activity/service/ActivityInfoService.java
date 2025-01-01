package com.micro.ssyx.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.activity.ActivityInfo;
import com.micro.ssyx.model.activity.ActivityRule;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.vo.activity.ActivityRuleVo;
import com.micro.ssyx.vo.order.CartInfoVo;
import com.micro.ssyx.vo.order.OrderConfirmVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    /**
     * 分页查询营销活动列表
     *
     * @param pageParam 分页参数
     * @return 营销活动列表
     */
    IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam);

    /**
     * 根据活动id获取活动规则列表
     *
     * @param activityId 活动id
     * @return 活动规则列表
     */
    Map<String, Object> findActivityRuleList(Long activityId);

    /**
     * 保存活动规则数据
     *
     * @param activityRuleVo 活动规则
     * @return 是否保存成功
     */
    Boolean saveActivityRule(ActivityRuleVo activityRuleVo);

    /**
     * 根据关键字查询sku信息
     *
     * @param keyword 关键字
     * @return sku信息
     */
    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    /**
     * 根据skuId列表获取促销信息
     *
     * @param skuIdList skuId列表
     * @return 促销信息
     */
    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    /**
     * 根据skuId获取促销信息
     *
     * @param skuId  skuId
     * @param userId 用户id
     * @return 促销信息
     */
    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    /**
     * 根据skuId获取活动规则数据
     *
     * @param skuId skuId
     * @return 活动规则数据
     */
    List<ActivityRule> findActivityRuleBySkuId(Long skuId);

    /**
     * 获取购物车对应促销信息
     *
     * @param cartInfoList 购物车信息
     * @param userId       用户id
     * @return 购物车对应促销信息
     */
    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);

    /**
     * 获取购物车对应的规则数据
     *
     * @param cartInfoList 购物车信息
     * @return 对应规则数据
     */
    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);
}
