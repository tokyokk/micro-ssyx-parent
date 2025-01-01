package com.micro.ssyx.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.product.service.CategoryService;
import com.micro.ssyx.vo.product.CategoryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品三级分类 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@RestController
@RequestMapping("/admin/product/category")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 商品分类列表
     *
     * @param page       当前页码
     * @param limit      每页记录数
     * @param categoryVo 查询条件
     * @return 商品分类列表
     */
    @GetMapping("{page}/{limit}")
    public ResultResponse<IPage<Category>> index(@PathVariable final Long page,
                                                 @PathVariable final Long limit,
                                                 final CategoryVo categoryVo) {
        final Page<Category> pageParam = new Page<>(page, limit);
        final IPage<Category> pageModel = categoryService.selectPageCategory(pageParam, categoryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 根据id获取商品分类信息
     *
     * @param id 商品分类id
     * @return 商品分类信息
     */
    @GetMapping("get/{id}")
    public ResultResponse<Category> get(@PathVariable final Long id) {
        final Category category = categoryService.getById(id);
        return ResultResponse.ok(category);
    }

    /**
     * 添加商品分类
     *
     * @param category 商品分类
     * @return 是否添加成功
     */
    @PostMapping("save")
    public ResultResponse<Boolean> save(@RequestBody final Category category) {
        return ResultResponse.ok(categoryService.save(category));
    }

    /**
     * 修改商品分类
     *
     * @param category 商品分类
     * @return 是否修改成功
     */
    @PutMapping("update")
    public ResultResponse<Boolean> updateById(@RequestBody final Category category) {
        return ResultResponse.ok(categoryService.updateById(category));
    }

    /**
     * 删除商品分类
     *
     * @param id 商品分类id
     * @return 是否删除成功
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Boolean> remove(@PathVariable final Long id) {
        return ResultResponse.ok(categoryService.removeById(id));
    }

    /**
     * 批量删除商品分类
     *
     * @param idList 商品分类id列表
     * @return 是否删除成功
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<Boolean> batchRemove(@RequestBody final List<Long> idList) {
        return ResultResponse.ok(categoryService.removeByIds(idList));
    }

    /**
     * 查询所有商品分类
     *
     * @return 商品分类列表
     */
    @GetMapping("findAllList")
    public ResultResponse<List<Category>> findAllList() {
        return ResultResponse.ok(categoryService.list());
    }
}

