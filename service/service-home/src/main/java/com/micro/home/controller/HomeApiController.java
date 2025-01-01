package com.micro.home.controller;

import com.micro.home.service.HomeService;
import com.micro.ssyx.common.auth.AuthContextHolder;
import com.micro.ssyx.common.result.ResultResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description 首页接口
 * @date 2024/6/24 20:06
 */
@RestController
@RequestMapping("/api/home")
public class HomeApiController {

    @Resource
    private HomeService homeService;

    /**
     * 首页数据显示
     *
     * @param request 请求
     * @return 首页数据
     */
    @RequestMapping("index")
    public ResultResponse<Map<String, Object>> index(final HttpServletRequest request) {
        final Long userId = AuthContextHolder.getUserId();
        final Map<String, Object> map = homeService.homeData(userId);
        return ResultResponse.ok(map);
    }
}
