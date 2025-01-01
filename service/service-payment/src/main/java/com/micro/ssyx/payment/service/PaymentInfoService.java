package com.micro.ssyx.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.enums.PaymentType;
import com.micro.ssyx.model.order.PaymentInfo;

import java.util.Map;

/**
 * @author micro
 * @description
 * @date 2024/7/8 19:16
 * @github https://github.com/tokyokk
 */
public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @param weixin
     * @return 支付记录
     */
    PaymentInfo getPaymentInfoByOrderNo(String orderNo, PaymentType weixin);

    /**
     * 保存支付记录
     *
     * @param orderNo     订单号
     * @param paymentType 支付类型
     * @return 支付记录
     */
    PaymentInfo savePaymentInfo(String orderNo, PaymentType paymentType);

    /**
     * 支付成功
     *
     * @param outTradeNo 订单号
     * @param resultMap  支付成功返回结果
     */
    void paySuccess(String outTradeNo, Map<String, String> resultMap);
}
