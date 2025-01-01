package com.micro.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.product.SkuAttrValue;
import com.micro.ssyx.product.mapper.SkuAttrValueMapper;
import com.micro.ssyx.product.service.SkuAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * spu属性值 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue> implements SkuAttrValueService {

    @Override
    public List<SkuAttrValue> getSkuAttrValueListBySkuId(final Long id) {
        return baseMapper.selectList(
                Wrappers.<SkuAttrValue>lambdaQuery().eq(SkuAttrValue::getSkuId, id)
        );
    }
}
