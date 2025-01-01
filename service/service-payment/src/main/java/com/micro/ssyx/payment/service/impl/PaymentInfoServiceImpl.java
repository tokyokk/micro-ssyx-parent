package com.micro.ssyx.payment.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.client.order.OrderFeignClient;
import com.micro.ssyx.common.exception.SsyxException;
import com.micro.ssyx.common.result.ResultCodeEnum;
import com.micro.ssyx.enums.PaymentStatus;
import com.micro.ssyx.enums.PaymentType;
import com.micro.ssyx.model.order.OrderInfo;
import com.micro.ssyx.model.order.PaymentInfo;
import com.micro.ssyx.mq.constant.MQConst;
import com.micro.ssyx.mq.service.RabbitService;
import com.micro.ssyx.payment.mapper.PaymentInfoMapper;
import com.micro.ssyx.payment.service.PaymentInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author micro
 * @description
 * @date 2024/7/8 19:17
 * @github https://github.com/tokyokk
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Resource
    private OrderFeignClient orderFeignClient;

    @Resource
    private RabbitService rabbitService;

    @Override
    public PaymentInfo getPaymentInfoByOrderNo(final String orderNo, final PaymentType weixin) {
        return baseMapper.selectOne(
                Wrappers.<PaymentInfo>lambdaQuery()
                        .eq(PaymentInfo::getOrderNo, orderNo)
                        .eq(PaymentInfo::getPaymentType, weixin)
        );
    }

    @Override
    public PaymentInfo savePaymentInfo(final String orderNo, final PaymentType paymentType) {
        // 远程调用根据orderNo查询订单信息
        final OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo);

        if (Objects.isNull(orderInfo)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }

        // 封装数据到PaymentInfo
        final PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        final String subject = "userId" + orderInfo.getUserId() + "pay" + orderInfo.getOrderNo();
        paymentInfo.setSubject(subject);
        // paymentInfo.setTotalAmount(order.getTotalAmount());
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));

        baseMapper.insert(paymentInfo);
        return paymentInfo;
    }

    @Override
    public void paySuccess(final String orderNo, final Map<String, String> resultMap) {
        // 1 查询当前订单支付记录表状态是否是已经支付
        final PaymentInfo paymentInfo = baseMapper.selectOne(
                Wrappers.<PaymentInfo>lambdaQuery().eq(PaymentInfo::getOrderNo, orderNo)
        );
        if (paymentInfo.getPaymentStatus() != PaymentStatus.UNPAID) {
            return;
        }

        // 2 如果支付记录表支付状态没有支付，更新
        paymentInfo.setPaymentStatus(PaymentStatus.PAID);
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);

        // 3 整合rabbitmq实现订单记录已经支付，库存扣减
        rabbitService.sendMessage(
                MQConst.EXCHANGE_PAY_DIRECT,
                MQConst.ROUTING_PAY_SUCCESS,
                orderNo
        );
    }
}
