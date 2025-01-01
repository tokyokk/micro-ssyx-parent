package com.micro.ssyx.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.sys.RegionWare;
import com.micro.ssyx.vo.sys.RegionWareQueryVo;

/**
 * <p>
 * 城市仓库关联表 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-10
 */
public interface RegionWareService extends IService<RegionWare> {

    /**
     * 开通区域列表
     *
     * @param pageParam         分页参数
     * @param regionWareQueryVo 查询条件
     * @return IPage<RegionWare>
     */

    IPage<RegionWare> selectRegionWarePageList(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo);

    /**
     * 添加开通区域
     *
     * @param regionWare 开通区域
     */
    void saveRegionWare(RegionWare regionWare);

    /**
     * 更新开通区域状态
     *
     * @param id     开通区域id
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
}
