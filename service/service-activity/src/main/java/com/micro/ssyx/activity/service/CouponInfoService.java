package com.micro.ssyx.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.activity.CouponInfo;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.vo.activity.CouponRuleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
public interface CouponInfoService extends IService<CouponInfo> {

    /**
     * 分页查询优惠券
     *
     * @param pageParam 分页参数
     * @return 优惠券列表
     */
    IPage<CouponInfo> selectPageCouponInfo(@Param("pageParam") Page<CouponInfo> pageParam);

    /**
     * 获取优惠券
     *
     * @param id 优惠券id
     * @return 优惠券信息
     */
    CouponInfo getCouponInfo(@Param("id") String id);

    /**
     * 获取优惠券规则列表
     *
     * @param id 优惠券id
     * @return 优惠券规则列表
     */
    Map<String, Object> findCouponRuleList(@Param("id") Long id);

    /**
     * 保存优惠券规则
     *
     * @param couponRuleVo 优惠券规则
     * @return 是否保存成功
     */
    Boolean saveCouponRule(@Param("couponRuleVo") CouponRuleVo couponRuleVo);

    /**
     * 根据skuId和userId查询优惠券
     *
     * @param skuId  skuId
     * @param userId 用户id
     * @return 优惠券列表
     */
    List<CouponInfo> findCouponInfoList(@Param("skuId") Long skuId, @Param("userId") Long userId);

    /**
     * 获取购物车可以使用的优惠卷列表
     *
     * @param cartInfoList 购物车列表
     * @param userId       用户id
     * @return 优惠券列表
     */
    List<CouponInfo> findCartCouponInfo(@Param("cartInfoList") List<CartInfo> cartInfoList, @Param("userId") Long userId);

    /**
     * 获取购物车对应优惠券信息
     *
     * @param cartInfoList 购物车列表
     * @param couponId     优惠券id
     * @return 优惠券信息
     */
    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

    /**
     * 更新优惠券使用状态
     *
     * @param couponId 优惠券id
     * @param userId   用户id
     * @param orderId  订单id
     * @return 是否更新成功
     */
    Boolean updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId);
}
