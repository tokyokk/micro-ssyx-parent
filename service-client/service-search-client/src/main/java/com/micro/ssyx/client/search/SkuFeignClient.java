package com.micro.ssyx.client.search;

import com.micro.ssyx.model.search.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 20:53
 */
@FeignClient("service-search")
public interface SkuFeignClient {

    /**
     * @return 爆品商品数据
     */
    @GetMapping("/api/search/sku/inner/findHotSkuList")
    List<SkuEs> findHotSkuList();

    /**
     * 更新商品热度
     *
     * @param skuId skuId
     * @return 是否更新成功
     */
    @GetMapping("/api/search/sku/inner/incrHotScore/{skuId}")
    Boolean incrHotScore(@PathVariable("skuId") final Long skuId);
}
