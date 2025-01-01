package com.micro.ssyx.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.sys.Region;

import java.util.List;

/**
 * <p>
 * 地区表 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-10
 */
public interface RegionService extends IService<Region> {

    /**
     * 根据关键词查询地区信息
     *
     * @param keyword 关键字
     * @return List<Region>
     */
    List<Region> findRegionByKeyword(String keyword);
}
