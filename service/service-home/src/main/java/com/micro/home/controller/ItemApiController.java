package com.micro.home.controller;

import com.micro.home.service.ItemService;
import com.micro.ssyx.common.auth.AuthContextHolder;
import com.micro.ssyx.common.result.ResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author micro
 * @description 商品详情
 * @date 2024/7/2 19:40
 * @github https://github.com/tokyokk
 */
@RestController
@RequestMapping("/api/home")
public class ItemApiController {

    @Resource
    private ItemService itemService;
    
    /**
     * 获取sku详细信息
     *
     * @param id skuId
     * @return sku详细信息
     */
    @GetMapping("item/{id}")
    public ResultResponse<Object> item(@PathVariable final Long id) {
        final Long userId = AuthContextHolder.getUserId();
        final Map<String, Object> map = itemService.item(id, userId);
        return ResultResponse.ok(map);
    }
}
