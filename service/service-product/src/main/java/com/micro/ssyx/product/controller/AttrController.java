package com.micro.ssyx.product.controller;


import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.product.Attr;
import com.micro.ssyx.product.service.AttrService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品属性 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@RestController
@RequestMapping("/admin/product/attr")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AttrController {

    private final AttrService attrService;

    /**
     * 根据分组id查询属性列表
     *
     * @param groupId 分组id
     * @return 分页列表数据
     */
    @GetMapping("{groupId}")
    public ResultResponse<List<Attr>> findAllListGroup(@PathVariable final Long groupId) {
        final List<Attr> attrList = attrService.findAttrListByGroupId(groupId);
        return ResultResponse.ok(attrList);
    }

    /**
     * 根据id查询属性
     *
     * @param id 属性id
     * @return 属性
     */
    @GetMapping("get/{id}")
    public ResultResponse<Attr> get(@PathVariable final Long id) {
        final Attr attr = attrService.getById(id);
        return ResultResponse.ok(attr);
    }

    /**
     * 新增属性
     *
     * @param attr 属性
     * @return 是否成功
     */
    @PostMapping("save")
    public ResultResponse<Boolean> save(@RequestBody final Attr attr) {
        return ResultResponse.ok(attrService.save(attr));
    }

    /**
     * 修改属性
     *
     * @param attr 属性
     * @return 是否成功
     */
    @PutMapping("update")
    public ResultResponse<Boolean> updateById(@RequestBody final Attr attr) {
        return ResultResponse.ok(attrService.updateById(attr));
    }

    /**
     * 删除属性
     *
     * @param id 属性id
     * @return 是否成功
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Boolean> remove(@PathVariable final Long id) {
        return ResultResponse.ok(attrService.removeById(id));
    }

    /**
     * 批量删除属性
     *
     * @param idList 属性id列表
     * @return 是否成功
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<Boolean> batchRemove(@RequestBody final List<Long> idList) {
        return ResultResponse.ok(attrService.removeByIds(idList));
    }
}

