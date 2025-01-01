package com.micro.ssyx.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.common.exception.SsyxException;
import com.micro.ssyx.common.result.ResultCodeEnum;
import com.micro.ssyx.model.sys.RegionWare;
import com.micro.ssyx.sys.mapper.RegionWareMapper;
import com.micro.ssyx.sys.service.RegionWareService;
import com.micro.ssyx.vo.sys.RegionWareQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 城市仓库关联表 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-10
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {

    @Override
    public IPage<RegionWare> selectRegionWarePageList(final Page<RegionWare> pageParam, final RegionWareQueryVo regionWareQueryVo) {
        final LambdaQueryWrapper<RegionWare> regionWareLambdaQueryWrapper = new LambdaQueryWrapper<>();

        regionWareLambdaQueryWrapper
                .like(!StringUtils.isEmpty(regionWareQueryVo.getKeyword()), RegionWare::getRegionName, regionWareQueryVo.getKeyword())
                .or().like(!StringUtils.isEmpty(regionWareQueryVo.getKeyword()), RegionWare::getWareName, regionWareQueryVo.getKeyword());
        return baseMapper.selectPage(pageParam, regionWareLambdaQueryWrapper);
    }

    @Override
    public void saveRegionWare(final RegionWare regionWare) {
        final LambdaQueryWrapper<RegionWare> regionWareLambdaQueryWrapper = new LambdaQueryWrapper<>();
        regionWareLambdaQueryWrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());
        if (baseMapper.selectCount(regionWareLambdaQueryWrapper) > 0) {
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        baseMapper.insert(regionWare);
    }

    @Override
    public void updateStatus(final Long id, final Integer status) {
        final RegionWare regionWare = baseMapper.selectById(id);
        regionWare.setStatus(status);
        baseMapper.updateById(regionWare);
    }
}
