package com.micro.ssyx.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.acl.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService extends IService<Permission>
{
    /**
     * 查询所有菜单
     *
     * @return 权限列表
     */
    List<Permission> queryAllPermission();

    /**
     * 递归删除菜单
     *
     * @param id 菜单id
     */
    void removeChildrenById(Long id);

    /**
     * 根据角色id给角色授权
     *
     * @param roleId 角色id
     */
    Map<String, Object> selectRolePermissionByRoleId(Long roleId);

    /**
     * 为角色授权
     *
     * @param roleId       角色id
     * @param permissionId 管理员id
     */
    void saveUserRoleRealtionShip(Long[] roleId, Long permissionId);
}
