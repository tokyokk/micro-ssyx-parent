package com.micro.ssyx.receiver;

import com.micro.ssyx.mq.constant.MQConst;
import com.micro.ssyx.service.CartInfoService;
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
 * @date 2024/7/8 18:27
 * @github https://github.com/tokyokk
 */
@Component
public class CartReceiver {

    @Resource
    private CartInfoService cartInfoService;

    /**
     * 根据用户id删除选中购物车记录
     *
     * @param userId  用户id
     * @param message 消息
     * @param channel 通道
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQConst.QUEUE_DELETE_CART, durable = "true"),
                    exchange = @Exchange(value = MQConst.EXCHANGE_ORDER_DIRECT),
                    key = {MQConst.ROUTING_DELETE_CART}
            )
    )
    public void deleteCart(final Long userId, final Message message, final Channel channel) throws IOException {
        if (userId != null) {
            cartInfoService.deleteCartChecked(userId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
