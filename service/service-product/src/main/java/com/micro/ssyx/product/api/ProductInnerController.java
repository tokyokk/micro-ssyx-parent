package com.micro.ssyx.product.api;

import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.product.service.CategoryService;
import com.micro.ssyx.product.service.SkuInfoService;
import com.micro.ssyx.vo.product.SkuInfoVo;
import com.micro.ssyx.vo.product.SkuStockLockVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author micro
 * @description
 * @date 2024/5/6 20:55
 * @github https://github.com/microsbug
 */
@RestController
@RequestMapping(value = "/api/product")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductInnerController {

    private final CategoryService categoryService;

    private final SkuInfoService skuInfoService;

    /**
     * 根据分类id获取分类信息
     *
     * @param categoryId 分类id
     * @return 商品分类
     */
    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategoryById(@PathVariable final Long categoryId) {
        return categoryService.getById(categoryId);
    }

    /**
     * 根据skuId获取sku信息
     *
     * @param skuId 商品id
     * @return 商品sku信息
     */
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfoById(@PathVariable final Long skuId) {
        return skuInfoService.getById(skuId);
    }

    /**
     * 根据skuId列表获取sku信息列表
     *
     * @param skuIdList 商品id列表
     * @return 商品sku信息列表
     */
    @PostMapping("inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody final List<Long> skuIdList) {
        return skuInfoService.findSkuInfoList(skuIdList);
    }

    /**
     * 根据分类id列表获取分类信息列表
     *
     * @param categoryIdList 分类id列表
     * @return 商品分类列表
     */
    @PostMapping("inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody final List<Long> categoryIdList) {
        return categoryService.listByIds(categoryIdList);
    }

    /**
     * 根据关键字查询sku信息
     *
     * @param keyword 关键字
     * @return sku信息
     */
    @GetMapping("inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") final String keyword) {
        return skuInfoService.findSkuInfoByKeyword(keyword);
    }

    /**
     * 获取分类信息
     *
     * @return 分类信息
     */
    @GetMapping("inner/findAllCategoryList")
    public List<Category> findAllCategoryList() {
        return categoryService.list();
    }

    /**
     * 获取新人专享
     *
     * @return 新人专享信息
     */
    @GetMapping("inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList() {
        return skuInfoService.findNewPersonList();
    }

    /**
     * 根据skuId获取sku信息
     *
     * @param skuId 商品id
     * @return 商品sku信息
     */
    @GetMapping("inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable final Long skuId) {
        return skuInfoService.getSkuInfoVo(skuId);
    }

    /**
     * 验证和锁定库存
     *
     * @param skuStockLockVoList 商品库存信息
     * @param orderNo            订单号
     * @return 是否锁定成功
     */
    @PostMapping("inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody final List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable("orderNo") final String orderNo) {
        return skuInfoService.checkAndLock(skuStockLockVoList, orderNo);
    }
}
