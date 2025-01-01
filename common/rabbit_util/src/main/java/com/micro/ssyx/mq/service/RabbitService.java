package com.micro.ssyx.mq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author micro
 * @description 消息发送服务
 * @date 2024/5/7 22:37
 * @github https://github.com/microsbug
 */
@Service
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息的方法
     *
     * @param exchange   交换机
     * @param routingKey 路由key
     * @param message    消息
     * @return 是否发送成功
     */
    public boolean sendMessage(final String exchange, final String routingKey, final Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
