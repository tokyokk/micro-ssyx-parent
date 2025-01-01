package com.micro.ssyx.client.product;

import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.vo.product.SkuInfoVo;
import com.micro.ssyx.vo.product.SkuStockLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author micro
 * @description
 * @date 2024/5/6 21:07
 * @github https://github.com/microsbug
 */
@FeignClient("service-product")
public interface ProductFeignClient {

    /**
     * 根据skuId获取sku信息
     *
     * @param skuId 商品id
     * @return 商品sku信息
     */
    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    SkuInfoVo getSkuInfoVo(@PathVariable final Long skuId);

    /**
     * 获取新人专享
     *
     * @return 新人专享信息
     */
    @GetMapping("/api/product/inner/findNewPersonSkuInfoList")
    List<SkuInfo> findNewPersonSkuInfoList();

    /**
     * 获取分类信息
     *
     * @return 分类信息
     */
    @GetMapping("/api/product/inner/findAllCategoryList")
    List<Category> findAllCategoryList();

    /**
     * 根据分类id获取分类信息
     *
     * @param categoryId 分类id
     * @return 商品分类
     */
    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    Category getCategoryById(@PathVariable("categoryId") final Long categoryId);

    /**
     * 根据skuId获取sku信息
     *
     * @param skuId 商品id
     * @return 商品sku信息
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable("skuId") final Long skuId);

    /**
     * 根据skuId列表获取sku信息列表
     *
     * @param skuIdList 商品id列表
     * @return 商品sku信息列表
     */
    @PostMapping("/api/product/inner/findSkuInfoList")
    List<SkuInfo> findSkuInfoList(@RequestBody final List<Long> skuIdList);

    /**
     * 根据关键字查询sku信息
     *
     * @param keyword 关键字
     * @return sku信息
     */
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") final String keyword);

    /**
     * 根据分类id列表获取分类信息列表
     *
     * @param categoryIdList 分类id列表
     * @return 商品分类列表
     */
    @PostMapping("/api/product/inner/findCategoryList")
    List<Category> findCategoryList(@RequestBody final List<Long> categoryIdList);

    /**
     * 验证和锁定库存
     *
     * @param skuStockLockVoList 商品库存信息
     * @param orderNo            订单号
     * @return 是否锁定成功
     */
    @PostMapping("/api/product/inner/checkAndLock/{orderNo}")
    Boolean checkAndLock(@RequestBody final List<SkuStockLockVo> skuStockLockVoList,
                         @PathVariable("orderNo") final String orderNo);
}
