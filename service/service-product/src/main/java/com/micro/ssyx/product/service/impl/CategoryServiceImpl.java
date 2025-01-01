package com.micro.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.product.mapper.CategoryMapper;
import com.micro.ssyx.product.service.CategoryService;
import com.micro.ssyx.vo.product.CategoryVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品三级分类 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public IPage<Category> selectPageCategory(final Page<Category> pageParam, final CategoryVo categoryVo) {
        final LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.like(categoryVo.getName() != null, Category::getName, categoryVo.getName());
        return baseMapper.selectPage(pageParam, categoryLambdaQueryWrapper);
    }
}
