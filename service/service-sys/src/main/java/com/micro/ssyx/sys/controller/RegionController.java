package com.micro.ssyx.sys.controller;


import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.sys.Region;
import com.micro.ssyx.sys.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 地区表 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-04-10
 */
@RestController
@RequestMapping("/admin/sys/region")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    /**
     * 根据关键字查询列表信息
     *
     * @param keyword 关键字
     */
    @GetMapping("findRegionByKeyword/{keyword}")
    public ResultResponse<List<Region>> findRegionByKeyword(@PathVariable("keyword") final String keyword) {
        final List<Region> list = regionService.findRegionByKeyword(keyword);
        return ResultResponse.ok(list);
    }
}

