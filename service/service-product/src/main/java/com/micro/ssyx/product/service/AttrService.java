package com.micro.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.product.Attr;

import java.util.List;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface AttrService extends IService<Attr> {

    /**
     * 根据分组id查询属性列表
     *
     * @param groupId 分组id
     * @return 列表数据
     */
    List<Attr> findAttrListByGroupId(Long groupId);
}
