package com.micro.ssyx.client.user;

import com.micro.ssyx.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 20:22
 */
@FeignClient("service-user")
public interface UserFeignClient {

    /**
     * 根据用户id查询提货点和团长信息
     *
     * @param userId 用户id
     * @return 提货点和团长信息
     */
    @GetMapping("/api/user/leader/inner/getUserAddressByUserId/{userId}")
    LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") final Long userId);
}
