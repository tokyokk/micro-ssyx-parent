package com.micro.home.service.impl;

import com.google.common.collect.Maps;
import com.micro.home.service.HomeService;
import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.client.search.SkuFeignClient;
import com.micro.ssyx.client.user.UserFeignClient;
import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.model.search.SkuEs;
import com.micro.ssyx.vo.user.LeaderAddressVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 20:07
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private SkuFeignClient skuFeignClient;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> homeData(final Long userId) {

        final Map<String, Object> resultMap = Maps.newHashMap();

        // 1.根据useId获取当前登录人提货地址信息
        final CompletableFuture<Void> leaderCompletableFuture = CompletableFuture.runAsync(() -> {
            final LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
            resultMap.put("leaderAddressVo", leaderAddressVo);
        }, threadPoolExecutor);

        // 2.获取所有分类
        final CompletableFuture<Void> categoryCompletableFuture = CompletableFuture.runAsync(() -> {
            final List<Category> allCategoryList = productFeignClient.findAllCategoryList();
            resultMap.put("categoryList", allCategoryList);
        }, threadPoolExecutor);


        // 3.获取新人专享商品
        final CompletableFuture<Void> newPersonCompletableFuture = CompletableFuture.runAsync(() -> {
            final List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
            resultMap.put("newPersonSkuInfoList", newPersonSkuInfoList);
        }, threadPoolExecutor);


        // 4.获取爆款商品
        final CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            final List<SkuEs> hotSkuList = skuFeignClient.findHotSkuList();
            resultMap.put("hotSkuList", hotSkuList);
        }, threadPoolExecutor);

        CompletableFuture.allOf(leaderCompletableFuture, categoryCompletableFuture, newPersonCompletableFuture, hotCompletableFuture).join();

        // 5.封装到map中返回
        return resultMap;
    }
}
