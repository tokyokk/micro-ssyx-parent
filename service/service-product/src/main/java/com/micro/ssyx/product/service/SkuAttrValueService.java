package com.micro.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.product.SkuAttrValue;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    /**
     * 根据skuId获取属性列表
     *
     * @param id skuId
     * @return 属性列表
     */
    List<SkuAttrValue> getSkuAttrValueListBySkuId(Long id);
}
