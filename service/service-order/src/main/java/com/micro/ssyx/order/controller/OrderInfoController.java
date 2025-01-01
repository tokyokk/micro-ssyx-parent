package com.micro.ssyx.order.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.common.auth.AuthContextHolder;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.order.OrderInfo;
import com.micro.ssyx.order.service.OrderInfoService;
import com.micro.ssyx.vo.order.OrderConfirmVo;
import com.micro.ssyx.vo.order.OrderSubmitVo;
import com.micro.ssyx.vo.order.OrderUserQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-07-08
 */
@RestController
@RequestMapping("/order/order")
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;

    @ApiOperation(value = "获取用户订单分页列表")
    @GetMapping("auth/findUserOrderPage/{page}/{limit}")
    public ResultResponse findUserOrderPage(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable final Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable final Long limit,

            @ApiParam(name = "orderVo", value = "查询对象", required = false) final
            OrderUserQueryVo orderUserQueryVo) {
        final Long userId = AuthContextHolder.getUserId();
        orderUserQueryVo.setUserId(userId);
        final Page<OrderInfo> pageParam = new Page<>(page, limit);
        final IPage<OrderInfo> pageModel = orderInfoService.findUserOrderPage(pageParam, orderUserQueryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 确认订单
     *
     * @return 订单信息
     */
    @GetMapping("auth/confirmOrder")
    public ResultResponse<OrderConfirmVo> confirm() {
        return ResultResponse.ok(orderInfoService.confirmOrder());
    }

    /**
     * 生成订单
     *
     * @param orderParamVo 订单信息
     * @return 订单Id
     */
    @PostMapping("auth/submitOrder")
    public ResultResponse<Long> submitOrder(@RequestBody final OrderSubmitVo orderParamVo) {
        return ResultResponse.ok(orderInfoService.submitOrder(orderParamVo));
    }

    /**
     * 根据订单Id查询订单信息
     *
     * @param orderId 订单Id
     * @return 订单信息
     */
    @GetMapping("auth/getOrderInfoById/{orderId}")
    public ResultResponse<OrderInfo> getOrderInfoById(@PathVariable("orderId") final Long orderId) {
        return ResultResponse.ok(orderInfoService.getOrderInfoById(orderId));
    }

    /**
     * 根据订单Id查询订单信息
     *
     * @param orderNo 订单Id
     * @return 订单信息
     */
    @GetMapping("inner/getOrderInfo/{orderNo}")
    public OrderInfo getOrderInfo(@PathVariable("orderNo") final String orderNo) {
        return orderInfoService.getOrderInfoByOrderNo(orderNo);
    }
}

