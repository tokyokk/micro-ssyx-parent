package com.micro.ssyx.payment.service;

import java.util.Map;

/**
 * @author micro
 * @description
 * @date 2024/7/8 19:15
 * @github https://github.com/tokyokk
 */
public interface WeixinService {

    /**
     * 调用微信支付系统生成预付单
     *
     * @param orderNo 订单号
     * @return 预付单
     */
    Map<String, String> createJsapi(String orderNo);

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号
     * @return 订单支付状态
     */
    Map<String, String> queryPayStatus(String orderNo);
}
