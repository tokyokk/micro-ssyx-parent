package com.micro.ssyx.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.acl.service.RoleService;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.acl.Role;
import com.micro.ssyx.vo.acl.RoleQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/acl/role")
public class RoleController {

    private final RoleService roleService;

    /**
     * 角色条件列表分页查询
     *
     * @param current     当前页码
     * @param limit       每页记录数
     * @param roleQueryVo 查询条件
     * @return 分页数据
     */
    @GetMapping("{current}/{limit}")
    public ResultResponse<IPage<Role>> getRoleList(@PathVariable final Long current,
                                                   @PathVariable final Long limit,
                                                   final RoleQueryVo roleQueryVo) {
        final Page<Role> page = new Page<>(current, limit);
        final IPage<Role> pageModel = roleService.selectRolePage(page, roleQueryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 根据id查询角色
     *
     * @param id 角色id
     * @return 角色
     */
    @GetMapping("get/{id}")
    public ResultResponse<Role> getRole(@PathVariable final Long id) {
        final Role role = roleService.getById(id);
        return ResultResponse.ok(role);
    }

    /**
     * 新增角色
     *
     * @param role 角色
     * @return 角色新增是否成功
     */
    @PostMapping("save")
    public ResultResponse<String> saveRole(@RequestBody final Role role) {
        final boolean isSuccess = roleService.save(role);
        if (!isSuccess) {
            return ResultResponse.fail("保存失败");
        }
        return ResultResponse.ok("保存成功");
    }

    /**
     * 修改角色
     *
     * @param role 角色
     * @return 角色更新是否成功
     */
    @PutMapping("update")
    public ResultResponse<String> updateRole(@RequestBody final Role role) {
        final boolean isSuccess = roleService.updateById(role);
        if (!isSuccess) {
            return ResultResponse.fail("修改失败");
        }
        return ResultResponse.ok("修改成功");
    }

    /**
     * 删除角色
     *
     * @param id 角色id
     * @return 角色是否删除成功
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<String> removeRole(@PathVariable final Long id) {
        final boolean isSuccess = roleService.removeById(id);
        if (!isSuccess) {
            return ResultResponse.fail("删除失败");
        }
        return ResultResponse.ok("删除成功");
    }

    /**
     * 批量删除角色
     *
     * @param idList 角色id列表
     * @return 角色批量删除是否成功
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<String> batchRemoveRole(@RequestBody final List<Long> idList) {
        final boolean isSuccess = roleService.removeByIds(idList);
        if (!isSuccess) {
            return ResultResponse.fail("批量删除失败");
        }
        return ResultResponse.ok("批量删除成功");
    }


}
