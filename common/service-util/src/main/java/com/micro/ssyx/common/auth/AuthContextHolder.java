package com.micro.ssyx.common.auth;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 19:43
 */

import com.micro.ssyx.vo.acl.AdminLoginVo;
import com.micro.ssyx.vo.user.UserLoginVo;

/**
 * 获取登录用户信息类ThreadLocal
 */
public class AuthContextHolder {
    // 会员用户id
    private static final ThreadLocal<Long> userId = new ThreadLocal<Long>();
    // 仓库id
    private static final ThreadLocal<Long> wareId = new ThreadLocal<>();
    // 会员基本信息
    private static final ThreadLocal<UserLoginVo> userLoginVo = new ThreadLocal<>();
    // 后台管理用户id
    private static final ThreadLocal<Long> adminId = new ThreadLocal<Long>();
    // 管理员基本信息
    private static final ThreadLocal<AdminLoginVo> adminLoginVo = new ThreadLocal<>();

    public static Long getUserId() {
        return userId.get();
    }

    public static void setUserId(final Long _userId) {
        userId.set(_userId);
    }

    public static Long getWareId() {
        return wareId.get();
    }

    public static void setWareId(final Long _wareId) {
        wareId.set(_wareId);
    }

    public static UserLoginVo getUserLoginVo() {
        return userLoginVo.get();
    }

    public static void setUserLoginVo(final UserLoginVo _userLoginVo) {
        userLoginVo.set(_userLoginVo);
    }

    public static Long getAdminId() {
        return adminId.get();
    }

    public static void setAdminId(final Long _adminId) {
        adminId.set(_adminId);
    }

    public static AdminLoginVo getAdminLoginVo() {
        return adminLoginVo.get();
    }

    public static void setAdminLoginVo(final AdminLoginVo _adminLoginVo) {
        adminLoginVo.set(_adminLoginVo);
    }
}