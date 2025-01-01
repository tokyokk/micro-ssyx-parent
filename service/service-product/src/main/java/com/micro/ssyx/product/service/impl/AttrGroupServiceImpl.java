package com.micro.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.product.AttrGroup;
import com.micro.ssyx.product.mapper.AttrGroupMapper;
import com.micro.ssyx.product.service.AttrGroupService;
import com.micro.ssyx.vo.product.AttrGroupQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 属性分组 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    @Override
    public IPage<AttrGroup> selectPage(final Page<AttrGroup> pageParam, final AttrGroupQueryVo attrGroupQueryVo) {
        final LambdaQueryWrapper<AttrGroup> attrGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        attrGroupLambdaQueryWrapper.like(!StringUtils.isEmpty(attrGroupQueryVo.getName()), AttrGroup::getName, attrGroupQueryVo.getName());
        return baseMapper.selectPage(pageParam, attrGroupLambdaQueryWrapper);
    }

    @Override
    public List<AttrGroup> findAllListGroup() {
        final LambdaQueryWrapper<AttrGroup> attrGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        attrGroupLambdaQueryWrapper.orderByDesc(AttrGroup::getSort);
        return baseMapper.selectList(attrGroupLambdaQueryWrapper);
    }
}
