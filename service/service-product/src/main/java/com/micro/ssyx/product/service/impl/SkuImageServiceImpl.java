package com.micro.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.product.SkuImage;
import com.micro.ssyx.product.mapper.SkuImageMapper;
import com.micro.ssyx.product.service.SkuImageService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 商品图片 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage> implements SkuImageService {

    @Override
    public List<SkuImage> getSkuImageListBySkuId(final Long id) {
        final LambdaQueryWrapper<SkuImage> skuImageLambdaQueryWrapper = Wrappers.lambdaQuery(SkuImage.class).eq(SkuImage::getSkuId, id);
        return baseMapper.selectList(skuImageLambdaQueryWrapper) == null ? Collections.emptyList() : baseMapper.selectList(skuImageLambdaQueryWrapper);
    }
}
