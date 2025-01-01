package com.micro.ssyx.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.sys.Region;
import com.micro.ssyx.sys.mapper.RegionMapper;
import com.micro.ssyx.sys.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 地区表 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-10
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Override
    public List<Region> findRegionByKeyword(final String keyword) {
        final LambdaQueryWrapper<Region> regionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        regionLambdaQueryWrapper.like(Region::getName, keyword);
        return this.list(regionLambdaQueryWrapper);
    }
}
