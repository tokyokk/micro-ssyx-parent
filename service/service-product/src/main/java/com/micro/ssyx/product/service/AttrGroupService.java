package com.micro.ssyx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.product.AttrGroup;
import com.micro.ssyx.vo.product.AttrGroupQueryVo;

import java.util.List;

/**
 * <p>
 * 属性分组 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface AttrGroupService extends IService<AttrGroup> {

    /**
     * 获取平台属性分页列表
     *
     * @param pageParam        分页参数
     * @param attrGroupQueryVo 查询条件
     * @return 分页列表数据
     */
    IPage<AttrGroup> selectPage(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo);

    /**
     * 获取所有平台属性分组
     *
     * @return 所有平台属性分组
     */
    List<AttrGroup> findAllListGroup();
}
