package com.micro.ssyx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.user.User;
import com.micro.ssyx.vo.user.LeaderAddressVo;
import com.micro.ssyx.vo.user.UserLoginVo;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/2 13:46
 */
public interface UserService extends IService<User> {
    /**
     * 根据openid查询用户信息
     *
     * @param openId 微信openid
     * @return 用户信息
     */
    User getByOpenid(String openId);

    /**
     * 根据用户id查询提货点和团长信息
     *
     * @param userId 用户id
     * @return 提货点和团长信息
     */
    LeaderAddressVo getLeaderAddressVoByUserId(Long userId);

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserLoginVo getUserLoginVo(Long userId);
}
