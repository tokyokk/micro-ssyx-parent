package com.micro.ssyx.order.receiver;

import com.micro.ssyx.mq.constant.MQConst;
import com.micro.ssyx.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author micro
 * @description
 * @date 2024/7/8 20:40
 * @github https://github.com/tokyokk
 */
@Component
public class OrderReceiver {

    @Resource
    private OrderInfoService orderInfoService;

    /**
     * 监听支付成功消息
     *
     * @param orderNo 订单号
     * @param message 消息
     * @param channel 通道
     * @throws IOException io异常
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQConst.QUEUE_ORDER_PAY, durable = "true"),
                    exchange = @Exchange(value = MQConst.EXCHANGE_PAY_DIRECT),
                    key = {MQConst.ROUTING_PAY_SUCCESS}
            )
    )
    public void paySuccess(final String orderNo,
                           final Message message,
                           final Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)) {
            orderInfoService.paySuccess(orderNo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
