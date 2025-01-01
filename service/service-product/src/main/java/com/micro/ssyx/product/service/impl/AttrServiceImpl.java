package com.micro.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.product.Attr;
import com.micro.ssyx.product.mapper.AttrMapper;
import com.micro.ssyx.product.service.AttrService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品属性 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    @Override
    public List<Attr> findAttrListByGroupId(final Long groupId) {
        final LambdaQueryWrapper<Attr> attrLambdaQueryWrapper = new LambdaQueryWrapper<>();
        attrLambdaQueryWrapper.eq(Attr::getAttrGroupId, groupId);
        return baseMapper.selectList(attrLambdaQueryWrapper);
    }
}
