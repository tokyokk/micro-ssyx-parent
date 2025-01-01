package com.micro.home.service.impl;

import com.micro.home.service.ItemService;
import com.micro.ssyx.activity.client.ActivityFeignClient;
import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.client.search.SkuFeignClient;
import com.micro.ssyx.vo.product.SkuInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author micro
 * @description
 * @date 2024/7/2 19:41
 * @github https://github.com/tokyokk
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private ActivityFeignClient activityFeignClient;

    @Resource
    private SkuFeignClient skuFeignClient;

    @Override
    public Map<String, Object> item(final Long skuId, final Long userId) {
        final Map<String, Object> resultMap = new HashMap<>(16);

        final CompletableFuture<SkuInfoVo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 远程调用获取sku对应数据
            final SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(skuId);
            resultMap.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        }, threadPoolExecutor);

        final CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用获取优惠卷信息
            final Map<String, Object> activityMap = activityFeignClient.findActivityAndCoupon(skuId, userId);
            resultMap.putAll(activityMap);
        }, threadPoolExecutor);

        final CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用更新热度
            skuFeignClient.incrHotScore(skuId);
        }, threadPoolExecutor);

        // 任务组合
        CompletableFuture.allOf(skuInfoCompletableFuture, activityCompletableFuture, hotCompletableFuture).join();

        return resultMap;
    }
}
