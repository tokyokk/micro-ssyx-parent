package com.micro.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.product.SkuPoster;
import com.micro.ssyx.product.mapper.SkuPosterMapper;
import com.micro.ssyx.product.service.SkuPosterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {

    @Override
    public List<SkuPoster> getSkuPosterListBySkuId(final Long id) {
        return baseMapper.selectList(
                Wrappers.<SkuPoster>lambdaQuery().eq(SkuPoster::getSkuId, id)
        );
    }
}
