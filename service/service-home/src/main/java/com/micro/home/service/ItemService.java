package com.micro.home.service;

import java.util.Map;

/**
 * @author micro
 * @description
 * @date 2024/7/2 19:41
 * @github https://github.com/tokyokk
 */
public interface ItemService {
    /**
     * 获取sku详细信息
     *
     * @param id     skuId
     * @param userId 用户id
     * @return sku详细信息
     */
    Map<String, Object> item(Long id, Long userId);
}
