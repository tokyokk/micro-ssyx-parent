package com.micro.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.acl.mapper.AdminMapper;
import com.micro.ssyx.acl.service.AdminService;
import com.micro.ssyx.model.acl.Admin;
import com.micro.ssyx.vo.acl.AdminQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public IPage<Admin> selectPageList(final Page<Admin> pageParam, final AdminQueryVo adminQueryVo) {
        final String name = adminQueryVo.getName();
        final String username = adminQueryVo.getUsername();
        final LambdaQueryWrapper<Admin> adminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminLambdaQueryWrapper.like(!StringUtils.isEmpty(name), Admin::getName, name)
                .eq(!StringUtils.isEmpty(username), Admin::getUsername, username);
        return baseMapper.selectPage(pageParam, adminLambdaQueryWrapper);
    }
}
