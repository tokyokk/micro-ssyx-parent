package com.micro.ssyx.controller;

import com.micro.ssyx.activity.client.ActivityFeignClient;
import com.micro.ssyx.common.auth.AuthContextHolder;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.service.CartInfoService;
import com.micro.ssyx.vo.order.OrderConfirmVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author micro
 * @description
 * @date 2024/7/5 16:38
 * @github https://github.com/tokyokk
 */
@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Resource
    private CartInfoService cartInfoService;

    @Resource
    private ActivityFeignClient activityFeignClient;

    @GetMapping("cartList")
    public ResultResponse<List<CartInfo>> cartList() {
        final Long userId = AuthContextHolder.getUserId();
        return ResultResponse.ok(cartInfoService.getCartList(userId));
    }

    /**
     * 添加购物车
     *
     * @param skuId  skuId
     * @param skuNum skuNum
     * @return 是否添加成功
     */
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public ResultResponse<Boolean> addToCart(@PathVariable("skuId") final Long skuId,
                                             @PathVariable("skuNum") final Integer skuNum) {
        final Long userId = AuthContextHolder.getUserId();
        return ResultResponse.ok(cartInfoService.addToCart(userId, skuId, skuNum));
    }

    /**
     * 查询带优惠卷的购物车
     *
     * @return
     */
    @GetMapping("activityCartList")
    public ResultResponse<OrderConfirmVo> activityCartList() {
        // 获取用户Id
        final Long userId = AuthContextHolder.getUserId();
        final List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);

        final OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        return ResultResponse.ok(orderTradeVo);
    }

    /**
     * 删除购物车
     *
     * @param skuId skuId
     * @return 是否添加成功
     */
    @DeleteMapping("deleteCart/{skuId}")
    public ResultResponse<Boolean> deleteCart(@PathVariable("skuId") final Long skuId) {
        final Long userId = AuthContextHolder.getUserId();
        return ResultResponse.ok(cartInfoService.deleteCart(userId, skuId));
    }

    /**
     * 清空购物车
     *
     * @return 是否清空成功
     */
    @DeleteMapping("deleteAllCart")
    public ResultResponse<Boolean> deleteAllCart() {
        final Long userId = AuthContextHolder.getUserId();
        return ResultResponse.ok(cartInfoService.deleteAllCart(userId));
    }

    /**
     * 批量删除购物车
     *
     * @param skuIdList skuIdList
     * @return 是否添加成功
     */
    @GetMapping("batchDeleteCart")
    public ResultResponse<Boolean> batchDeleteCart(@RequestBody final List<Long> skuIdList) {
        final Long userId = AuthContextHolder.getUserId();
        return ResultResponse.ok(cartInfoService.batchDeleteCart(userId, skuIdList));
    }

    /**
     * 更新选中状态
     *
     * @param skuId     skuId
     * @param isChecked 是否选中
     * @return 是否添加成功
     */
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public ResultResponse<Boolean> checkCart(@PathVariable(value = "skuId") final Long skuId,
                                             @PathVariable(value = "isChecked") final Integer isChecked) {
        // 获取用户Id
        final Long userId = AuthContextHolder.getUserId();
        // 调用更新方法
        return ResultResponse.ok(cartInfoService.checkCart(userId, isChecked, skuId));
    }

    /**
     * 全选
     *
     * @param isChecked 是否选中
     * @return 是否添加成功
     */
    @GetMapping("checkAllCart/{isChecked}")
    public ResultResponse<Boolean> checkAllCart(@PathVariable(value = "isChecked") final Integer isChecked) {
        // 获取用户Id
        final Long userId = AuthContextHolder.getUserId();
        // 调用更新方法
        return ResultResponse.ok(cartInfoService.checkAllCart(userId, isChecked));
    }

    /**
     * 批量选择购物车
     *
     * @param skuIdList skuIdList
     * @param isChecked 是否选中
     * @return 是否添加成功
     */
    @PostMapping("batchCheckCart/{isChecked}")
    public ResultResponse<Boolean> batchCheckCart(@RequestBody final List<Long> skuIdList,
                                                  @PathVariable(value = "isChecked") final Integer isChecked) {
        // 如何获取userId
        final Long userId = AuthContextHolder.getUserId();
        return ResultResponse.ok(cartInfoService.batchCheckCart(skuIdList, userId, isChecked));
    }

    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId userId
     * @return 购物车列表
     */
    @GetMapping("inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") final Long userId) {
        return cartInfoService.getCartCheckedList(userId);
    }
}
