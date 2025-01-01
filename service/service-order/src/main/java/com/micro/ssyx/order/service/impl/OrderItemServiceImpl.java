package com.micro.ssyx.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.order.OrderItem;
import com.micro.ssyx.order.mapper.OrderItemMapper;
import com.micro.ssyx.order.service.OrderItemService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单项信息 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-07-08
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
