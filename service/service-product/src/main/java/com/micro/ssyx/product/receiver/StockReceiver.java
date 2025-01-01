package com.micro.ssyx.product.receiver;

import com.micro.ssyx.mq.constant.MQConst;
import com.micro.ssyx.product.service.SkuInfoService;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
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
 * @date 2024/7/8 20:54
 * @github https://github.com/tokyokk
 */
@Component
public class StockReceiver {

    @Resource
    private SkuInfoService skuInfoService;

    /**
     * 扣减库存成功，更新订单状态
     *
     * @param orderNo
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_MINUS_STOCK, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_ORDER_DIRECT),
            key = {MQConst.ROUTING_MINUS_STOCK}
    ))
    public void minusStock(final String orderNo, final Message message, final Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)) {
            skuInfoService.minusStock(orderNo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}