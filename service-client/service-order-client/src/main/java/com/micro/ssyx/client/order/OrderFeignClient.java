package com.micro.ssyx.client.order;

import com.micro.ssyx.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author micro
 * @description
 * @date 2024/7/8 19:42
 * @github https://github.com/tokyokk
 */
@FeignClient(value = "service-order")
public interface OrderFeignClient {

    /**
     * 根据订单Id查询订单信息
     *
     * @param orderNo 订单Id
     * @return 订单信息
     */
    @GetMapping("/api/order/inner/getOrderInfo/{orderNo}")
    OrderInfo getOrderInfo(@PathVariable("orderNo") final String orderNo);
}
