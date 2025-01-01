package com.micro.ssyx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.acl.Admin;
import com.micro.ssyx.vo.acl.AdminQueryVo;

public interface AdminService extends IService<Admin> {
    
    /**
     * 分页查询用户数据
     *
     * @param pageParam    分页参数
     * @param adminQueryVo 查询条件
     * @return 用户分页列表数据
     */
    IPage<Admin> selectPageList(Page<Admin> pageParam, AdminQueryVo adminQueryVo);
}
