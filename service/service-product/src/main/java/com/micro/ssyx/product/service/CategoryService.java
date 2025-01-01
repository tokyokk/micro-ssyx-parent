package com.micro.ssyx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.vo.product.CategoryVo;

/**
 * <p>
 * 商品三级分类 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface CategoryService extends IService<Category> {

    /**
     * 商品分类列表
     *
     * @param pageParam  分页参数
     * @param categoryVo 查询条件
     * @return 分页列表
     */
    IPage<Category> selectPageCategory(Page<Category> pageParam, CategoryVo categoryVo);
}
