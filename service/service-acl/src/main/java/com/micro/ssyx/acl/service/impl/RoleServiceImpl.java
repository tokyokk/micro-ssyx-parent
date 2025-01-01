package com.micro.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.micro.ssyx.acl.mapper.RoleMapper;
import com.micro.ssyx.acl.service.AdminRoleService;
import com.micro.ssyx.acl.service.RoleService;
import com.micro.ssyx.model.acl.AdminRole;
import com.micro.ssyx.model.acl.Role;
import com.micro.ssyx.vo.acl.RoleQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;

    private final AdminRoleService adminRoleService;

    @Override
    public IPage<Role> selectRolePage(final Page<Role> pageParam, final RoleQueryVo roleQueryVo) {

        final String roleName = roleQueryVo.getRoleName();
        final LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.like(!StringUtils.isEmpty(roleName), Role::getRoleName, roleName);
        return roleMapper.selectPage(pageParam, roleLambdaQueryWrapper);
    }

    @Override
    public Map<String, Object> selectUserRoleByAdminId(final Long adminId) {
        // 1 查询所有角色
        final List<Role> allRolesList = baseMapper.selectList(null);

        // 2 根据用户id查询拥有的角色id列表
        // 2.1 根据用户id查询 用户角色关系表 admin_role 查询用户分配角色id列表
        final LambdaQueryWrapper<AdminRole> adminRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminRoleLambdaQueryWrapper.eq(AdminRole::getAdminId, adminId);
        final List<AdminRole> adminRoleList = adminRoleService.list(adminRoleLambdaQueryWrapper);

        // 2.2 通过第一步返回集合,获取所有角色id的列表 List<AdminRole> --> List<Long>
        final List<Long> roleIdsList = adminRoleList.stream().map(AdminRole::getRoleId).collect(Collectors.toList());

        // 2.3 创建新的list集合,用于存储用户配置角色
        final ArrayList<Role> assignRoleList = new ArrayList<>();

        // 2.4 遍历所有角色列表 allRoleList,得到每个角色
        // 判断角色id列表是否包含已经分配,封装到2.3里面新的list集合
        allRolesList.forEach(role -> {
            if (roleIdsList.contains(role.getId())) {
                assignRoleList.add(role);
            }
        });

        final HashMap<String, Object> result = Maps.newHashMap();
        // 所有角色
        result.put("allRolesList", allRolesList);
        // 用户已经分配的角色
        result.put("assignRoles", assignRoleList);

        return result;
    }

    @Override
    public void saveUserRoleRealtionShip(final Long[] roleIds, final Long adminId) {
        // 1 删除已经分配过的角色数据
        // 根据角色id删除admin_role表中的数据
        final LambdaQueryWrapper<AdminRole> adminRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminRoleLambdaQueryWrapper.eq(AdminRole::getAdminId, adminId);
        adminRoleService.remove(adminRoleLambdaQueryWrapper);

        // 遍历多个角色id,得到每个角色,拿着每个角色id + 用户id 添加到角色用户关系表中
        final List<AdminRole> list = new ArrayList<>();
        Arrays.stream(roleIds).forEachOrdered(roleId -> {
            final AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            list.add(adminRole);
        });
        adminRoleService.saveBatch(list);
    }
}
