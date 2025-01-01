package com.micro.ssyx.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.product.AttrGroup;
import com.micro.ssyx.product.service.AttrGroupService;
import com.micro.ssyx.vo.product.AttrGroupQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性分组 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@RestController
@RequestMapping("/admin/product/attrGroup")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AttrGroupController {

    private final AttrGroupService attrGroupService;

    /**
     * 获取平台属性分页列表
     *
     * @param page             当前页码
     * @param limit            每页记录数
     * @param attrGroupQueryVo 查询对象
     * @return 分页列表数据
     */
    @GetMapping("{page}/{limit}")
    public ResultResponse<IPage<AttrGroup>> index(@PathVariable final Long page,
                                                  @PathVariable final Long limit,
                                                  final AttrGroupQueryVo attrGroupQueryVo) {
        final Page<AttrGroup> pageParam = new Page<>(page, limit);
        final IPage<AttrGroup> pageModel = attrGroupService.selectPage(pageParam, attrGroupQueryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 获取平台属性分组信息
     *
     * @param id id
     * @return 平台属性分组信息
     */
    @GetMapping("get/{id}")
    public ResultResponse<AttrGroup> get(@PathVariable final Long id) {
        final AttrGroup attrGroup = attrGroupService.getById(id);
        return ResultResponse.ok(attrGroup);
    }

    /**
     * 新增平台属性分组
     *
     * @param attrGroup 平台属性分组
     * @return 新增结果
     */
    @PostMapping("save")
    public ResultResponse<Boolean> save(@RequestBody final AttrGroup attrGroup) {
        return ResultResponse.ok(attrGroupService.save(attrGroup));
    }

    /**
     * 修改平台属性分组
     *
     * @param attrGroup 平台属性分组
     * @return 修改结果
     */
    @PutMapping("update")
    public ResultResponse<Boolean> updateById(@RequestBody final AttrGroup attrGroup) {
        return ResultResponse.ok(attrGroupService.updateById(attrGroup));
    }

    /**
     * 删除平台属性分组
     *
     * @param id id
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Boolean> remove(@PathVariable final Long id) {
        return ResultResponse.ok(attrGroupService.removeById(id));
    }

    /**
     * 批量删除平台属性分组
     *
     * @param idList id列表
     * @return 删除结果
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<Boolean> batchRemove(@RequestBody final List<Long> idList) {
        return ResultResponse.ok(attrGroupService.removeByIds(idList));
    }

    /**
     * 获取所有平台属性分组
     *
     * @return 所有平台属性分组
     */
    @GetMapping("findAllList")
    public ResultResponse<List<AttrGroup>> findAllListGroup() {
        return ResultResponse.ok(attrGroupService.findAllListGroup());
    }
}

