package com.micro.ssyx.activity.api;

import com.micro.ssyx.activity.service.ActivityInfoService;
import com.micro.ssyx.activity.service.CouponInfoService;
import com.micro.ssyx.model.activity.CouponInfo;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.vo.order.CartInfoVo;
import com.micro.ssyx.vo.order.OrderConfirmVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author micro
 * @description
 * @date 2024/7/2 18:20
 * @github https://github.com/tokyokk
 */
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor(onConstructor_ = @Resource)
public class ActivityInfoApiController {

    private final ActivityInfoService activityInfoService;

    private final CouponInfoService couponInfoService;

    /**
     * 根据skuId获取促销列表
     *
     * @param skuIdList skuId集合
     * @return 促销列表信息
     */
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody final List<Long> skuIdList) {
        return activityInfoService.findActivity(skuIdList);
    }

    /**
     * 根据skuId获取营销数据与优惠卷
     *
     * @param skuId skuId
     * @return 营销数据与优惠卷
     */
    @GetMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable("skuId") final Long skuId, @PathVariable("userId") final Long userId) {
        return activityInfoService.findActivityAndCoupon(skuId, userId);
    }

    /**
     * 获取购物车中满足优惠券条件的优惠券列表
     *
     * @param cartInfoList 购物车列表
     * @param userId       用户id
     * @return 满足优惠券条件的优惠券列表
     */
    @PostMapping("inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody final List<CartInfo> cartInfoList, @PathVariable("userId") final Long userId) {
        return activityInfoService.findCartActivityAndCoupon(cartInfoList, userId);
    }

    /**
     * 获取购物车对应的规则数据
     *
     * @param cartInfoList 购物车信息
     * @return 对应规则数据
     */
    @PostMapping("inner/findCartActivityList")
    public List<CartInfoVo> findCartActivityList(@RequestBody final List<CartInfo> cartInfoList) {
        return activityInfoService.findCartActivityList(cartInfoList);
    }

    /**
     * 获取购物车对应优惠券列表
     *
     * @param cartInfoList 购物车信息
     * @param couponId     优惠券id
     * @return 优惠券列表
     */
    @PostMapping("/inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody final List<CartInfo> cartInfoList, @PathVariable("couponId") final Long couponId) {
        return couponInfoService.findRangeSkuIdList(cartInfoList, couponId);
    }

    /**
     * 更新优惠券使用状态
     *
     * @param couponId 优惠券id
     * @param userId   用户id
     * @param orderId  订单id
     * @return 是否更新成功
     */
    @GetMapping("/inner/updateCouponInfoUseStatus/{couponId}/{userId}/{orderId}")
    public Boolean updateCouponInfoUseStatus(
            @PathVariable("couponId") final Long couponId,
            @PathVariable("userId") final Long userId,
            @PathVariable("orderId") final Long orderId) {
        return couponInfoService.updateCouponInfoUseStatus(couponId, userId, orderId);
    }
}
