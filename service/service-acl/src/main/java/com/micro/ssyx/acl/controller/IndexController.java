package com.micro.ssyx.acl.controller;

import com.google.common.collect.Maps;
import com.micro.ssyx.common.result.ResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/acl/index")
public class IndexController {

    @PostMapping("/login")
    public ResultResponse<Map<String, String>> login() {
        final Map<String, String> map = Maps.newHashMap();
        map.put("token", "admin-token");
        return ResultResponse.ok(map);
    }

    @GetMapping("/info")
    public ResultResponse<Map<String, String>> info() {
        final Map<String, String> map = Maps.newHashMap();
        map.put("name", "admin");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return ResultResponse.ok(map);
    }

    @PostMapping("logout")
    public ResultResponse<String> logout() {
        return ResultResponse.ok(null);
    }
}
