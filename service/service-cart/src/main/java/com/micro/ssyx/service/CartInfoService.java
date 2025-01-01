package com.micro.ssyx.service;

import com.micro.ssyx.model.order.CartInfo;

import java.util.List;

/**
 * @author micro
 * @description
 * @date 2024/7/5 16:39
 * @github https://github.com/tokyokk
 */
public interface CartInfoService {
    /**
     * 添加商品到购物车
     *
     * @param userId 用户id
     * @param skuId  skuId
     * @param skuNum 商品数量
     * @return Boolean
     */
    Boolean addToCart(Long userId, Long skuId, Integer skuNum);

    /**
     * 删除购物车
     *
     * @param userId 用户id
     * @param skuId  skuId
     * @return Boolean
     */
    Boolean deleteCart(Long userId, Long skuId);

    /**
     * 删除购物车
     *
     * @param userId 用户id
     * @return Boolean
     */
    Boolean deleteAllCart(Long userId);

    /**
     * 批量删除购物车
     *
     * @param userId    用户id
     * @param skuIdList skuIdList
     * @return Boolean
     */
    Boolean batchDeleteCart(Long userId, List<Long> skuIdList);

    /**
     * 获取购物车列表
     *
     * @param userId 用户id
     * @return List<CartInfo>
     */
    List<CartInfo> getCartList(Long userId);

    /**
     * 选中购物车
     *
     * @param userId    用户id
     * @param isChecked 选中状态
     * @param skuId     skuId
     * @return Boolean
     */
    Boolean checkCart(Long userId, Integer isChecked, Long skuId);

    /**
     * 全选购物车
     *
     * @param userId    用户id
     * @param isChecked 选中状态
     * @return Boolean
     */

    Boolean checkAllCart(Long userId, Integer isChecked);

    /**
     * 批量选中购物车
     *
     * @param skuIdList skuIdList
     * @param userId    用户id
     * @param isChecked 选中状态
     * @return Boolean
     */
    Boolean batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked);

    /**
     * 获取购物车选中列表
     *
     * @param userId 用户id
     * @return List<CartInfo>
     */
    List<CartInfo> getCartCheckedList(Long userId);

    /**
     * 删除选中购物车
     *
     * @param userId 用户id
     * @return Boolean
     */
    void deleteCartChecked(Long userId);
}
