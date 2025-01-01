package com.micro.ssyx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.acl.Role;
import com.micro.ssyx.vo.acl.RoleQueryVo;

import java.util.Map;

public interface RoleService extends IService<Role> {

    /**
     * 角色列表分页查询
     *
     * @param pageParam   分页数据
     * @param roleQueryVo 查询条件
     * @return 分页数据
     */
    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);

    /**
     * 根据用户id获取角色数据
     *
     * @param adminId 用户id
     * @return 角色数据
     */
    Map<String, Object> selectUserRoleByAdminId(Long adminId);

    /**
     * 为用户进行角色分配
     *
     * @param roleId  角色id
     * @param adminId 用户id
     */
    void saveUserRoleRealtionShip(Long[] roleId, Long adminId);
}
