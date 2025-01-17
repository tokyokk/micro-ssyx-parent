package com.micro.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.acl.mapper.RolePermissionMapper;
import com.micro.ssyx.acl.service.RolePermissionService;
import com.micro.ssyx.model.acl.RolePermission;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService
{
}