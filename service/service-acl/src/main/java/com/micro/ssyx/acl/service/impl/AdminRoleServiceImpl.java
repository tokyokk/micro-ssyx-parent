package com.micro.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.acl.mapper.AdminRoleMapper;
import com.micro.ssyx.acl.service.AdminRoleService;
import com.micro.ssyx.model.acl.AdminRole;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
