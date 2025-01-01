package com.micro.ssyx.search.receiver;


import com.micro.ssyx.mq.constant.MQConst;
import com.micro.ssyx.search.service.SkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author micro
 * @description
 * @date 2024/5/13 21:13
 */
@Component
public class SkuReceiver {

    @Resource
    private SkuService skuService;

    /**
     * 商品上架
     *
     * @param skuId   skuId
     * @param message 发送的消息
     * @param channel 通道
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_GOODS_UPPER, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_GOODS_DIRECT),
            key = {MQConst.ROUTING_GOODS_UPPER}
    ))
    public void upperSku(final Long skuId, final Message message, final Channel channel) {
        try {
            if (skuId != null) {
                skuService.upperSku(skuId);
            }
            // 手动确认消息,第二个参数表示是否批量确认,如果为true,则批量确认所有小于当前消息的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (final IOException e) {
            try {
                // 第二个参数表示是否批量拒绝,如果为true,则拒绝所有小于当前消息的消息, 第三个参数表示是否重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (final IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 商品下架
     *
     * @param skuId   skuId
     * @param message 发送的消息
     * @param channel 通道
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_GOODS_LOWER, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_GOODS_DIRECT),
            key = {MQConst.ROUTING_GOODS_LOWER}
    ))
    public void lowerSku(final Long skuId, final Message message, final Channel channel) {
        try {
            if (skuId != null) {
                skuService.lowerSku(skuId);
            }
            // 手动确认消息,第二个参数表示是否批量确认,如果为true,则批量确认所有小于当前消息的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (final IOException e) {
            try {
                // 第二个参数表示是否批量拒绝,如果为true,则拒绝所有小于当前消息的消息, 第三个参数表示是否重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (final IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
