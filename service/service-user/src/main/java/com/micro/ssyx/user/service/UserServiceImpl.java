package com.micro.ssyx.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.model.user.Leader;
import com.micro.ssyx.model.user.User;
import com.micro.ssyx.model.user.UserDelivery;
import com.micro.ssyx.user.mapper.LeaderMapper;
import com.micro.ssyx.user.mapper.UserDeliveryMapper;
import com.micro.ssyx.user.mapper.UserMapper;
import com.micro.ssyx.vo.user.LeaderAddressVo;
import com.micro.ssyx.vo.user.UserLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/2 13:47
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserDeliveryMapper userDeliveryMapper;

    @Resource
    private LeaderMapper leaderMapper;

    @Override
    public User getByOpenid(final String openId) {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenId, openId)
        );
    }

    @Override
    public LeaderAddressVo getLeaderAddressVoByUserId(final Long userId) {
        // 根据用户id查询对应的团长id
        final UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>().eq(UserDelivery::getUserId, userId)
                        .eq(UserDelivery::getIsDefault, 1)
        );

        // 根据团长id查询团长其他信息
        if (userDelivery == null) {
            return null;
        }

        final Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());
        final LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(userId);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;
    }

    @Override
    public UserLoginVo getUserLoginVo(final Long userId) {
        final User user = baseMapper.selectById(userId);
        final UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setUserId(userId);
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setOpenId(user.getOpenId());
        userLoginVo.setIsNew(user.getIsNew());

        final UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>().eq(UserDelivery::getUserId, userId)
                        .eq(UserDelivery::getIsDefault, 1)
        );
        if (userDelivery != null) {
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
            userLoginVo.setWareId(userDelivery.getWareId());
        } else {
            userLoginVo.setLeaderId(1L);
            userLoginVo.setWareId(1L);
        }

        return userLoginVo;
    }
}
