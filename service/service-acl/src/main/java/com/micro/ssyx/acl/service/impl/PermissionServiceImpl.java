package com.micro.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.acl.mapper.PermissionMapper;
import com.micro.ssyx.acl.service.PermissionService;
import com.micro.ssyx.acl.service.RolePermissionService;
import com.micro.ssyx.acl.service.RoleService;
import com.micro.ssyx.model.acl.Permission;
import com.micro.ssyx.model.acl.RolePermission;
import com.micro.ssyx.utils.PermissionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService
{
    private final RolePermissionService rolePermissionService;

    private final RoleService roleService;

    @Override
    public List<Permission> queryAllPermission()
    {
        // 1 查询所有菜单
        final List<Permission> allPermissionList = baseMapper.selectList(null);

        // 2 转化成需要的数据格式
        return PermissionHelper.buildPermissions(allPermissionList);
    }

    @Override
    public void removeChildrenById(final Long id)
    {
        final ArrayList<Long> idList = new ArrayList<>();

        this.getAllPermissionId(id, idList);

        // 设置当前菜单的id
        idList.add(id);

        baseMapper.deleteBatchIds(idList);
    }

    @Override
    public void saveUserRoleRealtionShip(final Long[] roleId, final Long permissionId)
    {
        final LambdaQueryWrapper<RolePermission> adminRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminRoleLambdaQueryWrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionService.remove(adminRoleLambdaQueryWrapper);

        rolePermissionService.saveBatch(Arrays.stream(roleId).map(roleId1 -> {
            final RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(permissionId);
            rolePermission.setRoleId(roleId1);
            return rolePermission;
        }).collect(Collectors.toList()));
    }

    @Override
    public Map<String, Object> selectRolePermissionByRoleId(final Long roleId)
    {
        final List<Permission> allPermissionList = baseMapper.selectList(null);

        final LambdaQueryWrapper<RolePermission> rolePermissionQuery = new LambdaQueryWrapper<>();
        rolePermissionQuery.eq(RolePermission::getRoleId, roleId);
        final List<RolePermission> rolePermissionList = rolePermissionService.list(rolePermissionQuery);

        final List<Long> permissionIdsList = rolePermissionList.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());

        final ArrayList<Permission> assignPermissionList = new ArrayList<>();

        allPermissionList.forEach(permission -> {
            if (permissionIdsList.contains(permission.getId())) {
                assignPermissionList.add(permission);
            }
        });

        final HashMap<String, Object> result = new HashMap<>(2);
        result.put("allPermissions", allPermissionList);
        result.put("assignPermissionList", assignPermissionList);

        return result;
    }

    private void getAllPermissionId(final Long id, final ArrayList<Long> idList)
    {
        final LambdaQueryWrapper<Permission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        permissionLambdaQueryWrapper.eq(Permission::getPid, id);
        final List<Permission> permissions = baseMapper.selectList(permissionLambdaQueryWrapper);
        if (!permissions.isEmpty()) {
            permissions.forEach(permission -> {
                idList.add(permission.getId());
                this.getAllPermissionId(permission.getId(), idList);
            });
        }
    }
}
