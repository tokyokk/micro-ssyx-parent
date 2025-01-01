package com.micro.ssyx.sys.controller;


import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.sys.Ware;
import com.micro.ssyx.sys.service.WareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 仓库表 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-04-10
 */
@RestController
@RequestMapping("/admin/sys/ware")
@RequiredArgsConstructor
public class WareController {

    private final WareService wareService;

    /**
     * 查询所有仓库列表
     *
     * @return 仓库列表数据
     */
    @GetMapping("findAllList")
    public ResultResponse<List<Ware>> findAllList() {
        final List<Ware> list = wareService.list();
        return ResultResponse.ok(list);
    }
}

