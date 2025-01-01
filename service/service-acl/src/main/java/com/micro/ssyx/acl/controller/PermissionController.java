package com.micro.ssyx.acl.controller;


import com.micro.ssyx.acl.service.PermissionService;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.acl.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/acl/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * 根据角色获取菜单
     *
     * @param roleId 角色id
     * @return 菜单列表
     */
    @GetMapping("toAssign/{roleId}")
    public ResultResponse<Map<String, Object>> toAssign(@PathVariable final Long roleId) {
        final Map<String, Object> map = permissionService.selectRolePermissionByRoleId(roleId);
        return ResultResponse.ok(map);
    }

    @PostMapping("doAssign")
    public ResultResponse<Void> doAssign(@RequestParam final Long[] roleId,
                                         @RequestParam final Long permissionId) {
        permissionService.saveUserRoleRealtionShip(roleId, permissionId);
        return ResultResponse.ok(null);
    }

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    @GetMapping
    public ResultResponse<List<Permission>> queryAllPermission() {
        final List<Permission> list = permissionService.queryAllPermission();
        return ResultResponse.ok(list);
    }

    /**
     * 添加菜单
     *
     * @param permission 菜单
     * @return 菜单
     */
    @PostMapping("save")
    public ResultResponse<Permission> save(@RequestBody final Permission permission) {
        permissionService.save(permission);
        return ResultResponse.ok(null);
    }

    /**
     * 修改菜单
     *
     * @param permission 菜单
     * @return 菜单
     */
    @PutMapping("update")
    public ResultResponse<Permission> updateById(@RequestBody final Permission permission) {
        permissionService.updateById(permission);
        return ResultResponse.ok(null);
    }

    /**
     * 递归删除菜单
     *
     * @param id 菜单id
     * @return 菜单
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Permission> remove(@PathVariable final Long id) {
        permissionService.removeChildrenById(id);
        return ResultResponse.ok(null);
    }

}
