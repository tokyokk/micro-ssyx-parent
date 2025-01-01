package com.micro.ssyx.client.cart;

import com.micro.ssyx.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author micro
 * @description
 * @date 2024/7/8 15:35
 * @github https://github.com/tokyokk
 */
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId userId
     * @return 购物车列表
     */
    @GetMapping("/api/cart/inner/getCartCheckedList/{userId}")
    List<CartInfo> getCartCheckedList(@PathVariable("userId") final Long userId);
}
