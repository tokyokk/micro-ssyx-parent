package com.micro.ssyx.payment.controller;

import com.alibaba.nacos.common.utils.MapUtils;
import com.micro.ssyx.common.result.ResultCodeEnum;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.payment.service.PaymentInfoService;
import com.micro.ssyx.payment.service.WeixinService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author micro
 * @description 微信支付接口
 * @date 2024/7/8 19:14
 * @github https://github.com/tokyokk
 */
@RestController
@RequestMapping("/api/payment/weixin")
@Slf4j
public class WeixinController {

    @Resource
    private WeixinService weixinPayService;

    @Resource
    private PaymentInfoService paymentInfoService;

    /**
     * 调用微信支付系统生成预付单
     *
     * @param orderNo 订单号
     * @return 微信支付系统返回的预付单信息
     */
    @GetMapping("/createJsapi/{orderNo}")
    public ResultResponse<Map<String, String>> createJsapi(
            @ApiParam(name = "orderNo", value = "订单No", required = true)
            @PathVariable("orderNo") final String orderNo) {
        return ResultResponse.ok(weixinPayService.createJsapi(orderNo));
    }

    @GetMapping("queryPayStatus/{orderNo}")
    public ResultResponse queryPayStatus(@PathVariable("orderNo") final String orderNo) {
        // 1 调用微信支付系统接口查询订单支付状态
        final Map<String, String> resultMap = weixinPayService.queryPayStatus(orderNo);

        // 2 微信支付系统返回null，支付失败
        if (MapUtils.isEmpty(resultMap)) {
            return ResultResponse.build(null, ResultCodeEnum.PAYMENT_FAIL);
        }

        // 3 如果微信支付系统返回值，判断支付成功
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            final String outTradeNo = resultMap.get("out_trade_no");
            paymentInfoService.paySuccess(outTradeNo, resultMap);
            return ResultResponse.ok(null);
        }
        // 4 支付中，等待
        return ResultResponse.build(null, ResultCodeEnum.PAYMENT_WAITING);
    }
}
