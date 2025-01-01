package com.micro.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.product.SkuPoster;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface SkuPosterService extends IService<SkuPoster> {

    /**
     * 根据skuId获取海报列表
     *
     * @param id skuId
     * @return 海报列表
     */
    List<SkuPoster> getSkuPosterListBySkuId(Long id);
}
