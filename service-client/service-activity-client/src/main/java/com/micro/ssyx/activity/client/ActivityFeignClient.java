package com.micro.ssyx.activity.client;

import com.micro.ssyx.model.activity.CouponInfo;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.vo.order.CartInfoVo;
import com.micro.ssyx.vo.order.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author micro
 * @description
 * @date 2024/7/2 18:59
 * @github https://github.com/tokyokk
 */
@FeignClient(value = "service-activity")
public interface ActivityFeignClient {

    /**
     * 根据skuId获取促销列表
     *
     * @param skuIdList skuId集合
     * @return 促销列表信息
     */
    @PostMapping("/api/activity/inner/findActivity")
    Map<Long, List<String>> findActivity(@RequestBody final List<Long> skuIdList);

    /**
     * 根据skuId获取营销数据与优惠卷
     *
     * @param skuId skuId
     * @return 营销数据与优惠卷
     */
    @GetMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    Map<String, Object> findActivityAndCoupon(@PathVariable("skuId") final Long skuId,
                                              @PathVariable("userId") final Long userId);

    /**
     * 获取购物车中满足优惠券条件的优惠券列表
     *
     * @param cartInfoList 购物车列表
     * @param userId       用户id
     * @return 满足优惠券条件的优惠券列表
     */
    @PostMapping("/api/activity/inner/findCartActivityAndCoupon/{userId}")
    OrderConfirmVo findCartActivityAndCoupon(@RequestBody final List<CartInfo> cartInfoList,
                                             @PathVariable("userId") final Long userId);

    /**
     * 获取购物车对应的规则数据
     *
     * @param cartInfoList 购物车信息
     * @return 对应规则数据
     */
    @PostMapping("/api/activity/inner/findCartActivityList")
    List<CartInfoVo> findCartActivityList(@RequestBody final List<CartInfo> cartInfoList);

    /**
     * 获取购物车对应优惠券列表
     *
     * @param cartInfoList 购物车信息
     * @param couponId     优惠券id
     * @return 优惠券列表
     */
    @PostMapping("/api/activity/inner/findRangeSkuIdList/{couponId}")
    CouponInfo findRangeSkuIdList(@RequestBody final List<CartInfo> cartInfoList,
                                  @PathVariable("couponId") final Long couponId);

    /**
     * 更新优惠券使用状态
     *
     * @param couponId 优惠券id
     * @param userId   用户id
     * @param orderId  订单id
     * @return 是否更新成功
     */
    @GetMapping("/api/activity/inner/updateCouponInfoUseStatus/{couponId}/{userId}/{orderId}")
    Boolean updateCouponInfoUseStatus(@PathVariable("couponId") final Long couponId,
                                      @PathVariable("userId") final Long userId,
                                      @PathVariable("orderId") final Long orderId);
}
