package com.micro.ssyx.service.impl;

import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.common.constant.RedisConst;
import com.micro.ssyx.common.exception.SsyxException;
import com.micro.ssyx.common.result.ResultCodeEnum;
import com.micro.ssyx.enums.SkuType;
import com.micro.ssyx.model.base.BaseEntity;
import com.micro.ssyx.model.order.CartInfo;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.service.CartInfoService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author micro
 * @description
 * @date 2024/7/5 16:40
 * @github https://github.com/tokyokk
 */
@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ProductFeignClient productFeignClient;

    @Override
    public Boolean addToCart(final Long userId, final Long skuId, Integer skuNum) {
        final String cartKey = this.getCartKey(userId);

        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        final CartInfo cartInfo;
        if (Boolean.TRUE.equals(hashOperations.hasKey(skuId.toString()))) {
            cartInfo = hashOperations.get(skuId.toString());
            final Integer currentSkuNum = cartInfo.getSkuNum() + skuNum;

            if (currentSkuNum < 1) {
                return Boolean.FALSE;
            }

            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);

            // 判断限购数量
            final Integer perLimit = cartInfo.getPerLimit();
            if (currentSkuNum > perLimit) {
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());

        } else {
            // 没有skuId，第一次添加
            skuNum = 1;

            final SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
            if (Objects.isNull(skuInfo)) {
                throw new SsyxException(ResultCodeEnum.DATA_ERROR);
            }
            cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }
        // 更新redis数据
        hashOperations.put(skuId.toString(), cartInfo);

        // 设置过期时间
        this.setCartKeyExpire(cartKey);

        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteCart(final Long userId, final Long skuId) {
        final BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(this.getCartKey(userId));
        if (Boolean.TRUE.equals(boundHashOperations.hasKey(skuId.toString()))) {
            boundHashOperations.delete(skuId.toString());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean deleteAllCart(final Long userId) {
        final String cartKey = this.getCartKey(userId);
        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        Objects.requireNonNull(hashOperations.values()).forEach(
                cartInfo -> hashOperations.delete(cartInfo.getSkuId().toString())
        );
        return Boolean.TRUE;
    }

    @Override
    public Boolean batchDeleteCart(final Long userId, final List<Long> skuIdList) {
        final String cartKey = this.getCartKey(userId);
        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(
                skuId -> {
                    hashOperations.delete(skuId.toString());
                }
        );
        return Boolean.TRUE;
    }

    @Override
    public List<CartInfo> getCartList(final Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(String.valueOf(userId))) {
            return cartInfoList;
        }

        // 从redis中获取key
        final String cartKey = this.getCartKey(userId);
        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        cartInfoList = hashOperations.values();
        if (!Collections.isEmpty(cartInfoList)) {
            cartInfoList.sort(
                    Comparator.comparing(BaseEntity::getCreateTime)
            );
        }
        return cartInfoList;
    }

    @Override
    public Boolean checkCart(final Long userId, final Integer isChecked, final Long skuId) {
        // 获取redis中的key
        final String cartKey = this.getCartKey(userId);
        // 通过cartKey获取hash操作对象field-value
        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        // 根据field：skuId获取value：购物车信息
        final CartInfo cartInfo = hashOperations.get(skuId.toString());

        Optional.ofNullable(cartInfo).ifPresent(
                new Consumer<CartInfo>() {
                    @Override
                    public void accept(final CartInfo cart) {
                        cart.setIsChecked(isChecked);
                        hashOperations.put(skuId.toString(), cart);
                        CartInfoServiceImpl.this.setCartKeyExpire(cartKey);
                    }
                }
        );
        return Boolean.TRUE;
    }

    @Override
    public Boolean checkAllCart(final Long userId, final Integer isChecked) {
        // 获取redis中的key
        final String cartKey = this.getCartKey(userId);
        // 通过cartKey获取hash操作对象field-value
        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        Objects.requireNonNull(hashOperations.values()).forEach(
                cartInfo -> {
                    cartInfo.setIsChecked(isChecked);
                    hashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
                }
        );
        this.setCartKeyExpire(cartKey);
        return Boolean.TRUE;
    }

    @Override
    public Boolean batchCheckCart(final List<Long> skuIdList, final Long userId, final Integer isChecked) {
        final String cartKey = this.getCartKey(userId);
        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(
                skuId -> {
                    final CartInfo cartInfo = hashOperations.get(skuId.toString());
                    cartInfo.setIsChecked(isChecked);
                    hashOperations.put(skuId.toString(), cartInfo);
                }
        );
        this.setCartKeyExpire(cartKey);
        return Boolean.TRUE;
    }

    @Override
    public List<CartInfo> getCartCheckedList(final Long userId) {

        final String cartKey = this.getCartKey(userId);
        final BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);

        return Objects.requireNonNull(hashOperations.values())
                .stream()
                .filter(cartInfo -> cartInfo.getIsChecked().equals(1))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCartChecked(final Long userId) {
        final List<CartInfo> cartInfoList = this.getCartCheckedList(userId);

        final List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());

        final String cartKey = this.getCartKey(userId);

        final BoundHashOperations<String, String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);

        skuIdList.forEach(
                skuId -> boundHashOperations.delete(skuId.toString())
        );
    }

    /**
     * 获取购物车key
     *
     * @param userId 用户id
     * @return 购物车key
     */
    private String getCartKey(final Long userId) {
        return String.format("%s%d%s", RedisConst.USER_KEY_PREFIX, userId, RedisConst.USER_CART_KEY_SUFFIX);
    }

    /**
     * 设置购物车key的过期时间
     *
     * @param key redis key
     */
    private void setCartKeyExpire(final String key) {
        redisTemplate.expire(key, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }
}
