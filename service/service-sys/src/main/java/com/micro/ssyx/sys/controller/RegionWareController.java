package com.micro.ssyx.sys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.sys.RegionWare;
import com.micro.ssyx.sys.service.RegionWareService;
import com.micro.ssyx.vo.sys.RegionWareQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 城市仓库关联表 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-04-10
 */
@RestController
@RequestMapping("/admin/sys/regionWare")
@RequiredArgsConstructor
public class RegionWareController {

    private final RegionWareService regionWareService;

    /**
     * 开通区域列表接口
     *
     * @param page              当前页码
     * @param limit             每页记录数
     * @param regionWareQueryVo 查询条件
     * @return 区域列表
     */
    @GetMapping("{page}/{limit}")
    public ResultResponse<IPage<RegionWare>> getPageList(
            @PathVariable final Long page,
            @PathVariable final Long limit,
            final RegionWareQueryVo regionWareQueryVo) {
        final Page<RegionWare> pageParam = new Page<>(page, limit);
        final IPage<RegionWare> pageModel = regionWareService.selectRegionWarePageList(pageParam, regionWareQueryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 添加开通区域
     *
     * @param regionWare 开通区域
     * @return 添加结果
     */
    @PostMapping("save")
    public ResultResponse<Void> addRegionWare(@RequestBody final RegionWare regionWare) {
        regionWareService.saveRegionWare(regionWare);
        return ResultResponse.ok(null);
    }

    /**
     * 删除开通区域
     *
     * @param id 开通区域id
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Void> removeRegionWare(@PathVariable final Long id) {
        regionWareService.removeById(id);
        return ResultResponse.ok(null);
    }

    // 取消开通区域
    @DeleteMapping("updateStatus/{id}/{status}")
    public ResultResponse<Void> updateStatus(
            @PathVariable final Long id,
            @PathVariable final Integer status) {
        regionWareService.updateStatus(id, status);
        return ResultResponse.ok(null);
    }

}

