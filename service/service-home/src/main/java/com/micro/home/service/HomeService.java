package com.micro.home.service;

import java.util.Map;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 20:06
 */
public interface HomeService {
    /**
     * 首页数据
     *
     * @param userId 用户id
     * @return
     */
    Map<String, Object> homeData(Long userId);
}
