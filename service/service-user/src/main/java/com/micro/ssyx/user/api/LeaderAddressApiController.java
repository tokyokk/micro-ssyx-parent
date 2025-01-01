package com.micro.ssyx.user.api;

import com.micro.ssyx.user.service.UserService;
import com.micro.ssyx.vo.user.LeaderAddressVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 20:17
 */
@RestController
@RequestMapping("/api/user/leader")
public class LeaderAddressApiController {

    @Resource
    private UserService userService;

    /**
     * 根据用户id查询提货点和团长信息
     *
     * @param userId 用户id
     * @return 提货点和团长信息
     */
    @GetMapping("/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") final Long userId) {
        return userService.getLeaderAddressVoByUserId(userId);
    }
}
