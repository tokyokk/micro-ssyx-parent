package com.micro.ssyx.common.auth;

import com.micro.ssyx.common.constant.RedisConst;
import com.micro.ssyx.common.utils.JwtHelper;
import com.micro.ssyx.vo.user.UserLoginVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 19:46
 */
public class UserLoginInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    public UserLoginInterceptor(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        this.getUserLoginVo(request);
        return true;
    }

    private void getUserLoginVo(final HttpServletRequest request) {
        // 1.从请求头获取token
        final String token = request.getHeader("token");

        // 2.判断token是否为空
        if (StringUtils.isNotBlank(token)) {
            // 3.从token中获取userId
            final Long userId = JwtHelper.getUserId(token);
            // 4.根据userId到Redis中获取用户信息
            final UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);
            // 5.获取数据存放到ThreadLocal中
            if (userLoginVo != null) {
                AuthContextHolder.setUserId(userLoginVo.getUserId());
                AuthContextHolder.setWareId(userLoginVo.getWareId());
                AuthContextHolder.setUserLoginVo(userLoginVo);
            }
        }
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
