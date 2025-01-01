package com.micro.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.product.SkuImage;

import java.util.List;

/**
 * <p>
 * 商品图片 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface SkuImageService extends IService<SkuImage> {

    /**
     * 根据skuId获取sku图片列表
     *
     * @param id skuId
     * @return sku图片列表
     */
    List<SkuImage> getSkuImageListBySkuId(Long id);
}
