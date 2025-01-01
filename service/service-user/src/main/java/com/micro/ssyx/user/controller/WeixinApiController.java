package com.micro.ssyx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.micro.ssyx.common.auth.AuthContextHolder;
import com.micro.ssyx.common.constant.RedisConst;
import com.micro.ssyx.common.exception.SsyxException;
import com.micro.ssyx.common.result.ResultCodeEnum;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.common.utils.JwtHelper;
import com.micro.ssyx.enums.UserType;
import com.micro.ssyx.model.user.User;
import com.micro.ssyx.user.service.UserService;
import com.micro.ssyx.user.utils.ConstantPropertiesUtil;
import com.micro.ssyx.user.utils.HttpClientUtils;
import com.micro.ssyx.vo.user.LeaderAddressVo;
import com.micro.ssyx.vo.user.UserLoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("/wxLogin/{code}")
    public ResultResponse<Map<String, Object>> callback(@PathVariable final String code) {
        // 获取授权临时票据
        System.out.println("微信授权服务器回调。。。。。。" + code);
        if (StringUtils.isEmpty(code)) {
            throw new SsyxException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        // 使用code和appid以及appscrect换取access_token
        final StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");

        final String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        final String result;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (final Exception e) {
            throw new SsyxException(ResultCodeEnum.FETCH_ACCESSION_FAIL);
        }

        System.out.println("使用code换取的access_token结果 = " + result);
        final JSONObject resultJson = JSONObject.parseObject(result);
        if (resultJson.getString("errcode") != null) {
            throw new SsyxException(ResultCodeEnum.FETCH_ACCESSION_FAIL);
        }

        final String accessToken = resultJson.getString("session_key");
        final String openId = resultJson.getString("openid");

        // TODO 为了测试，openId写固定
        // final String accessToken = "";
        // final String openId = "odo3j4uGJf6Hl2FopkEOLGxr7LE4";

        // 根据access_token获取微信用户的基本信息
        // 先根据openid进行数据库查询
        User user = userService.getByOpenid(openId);
        // 如果没有查到用户信息,那么调用微信个人信息获取的接口
        if (null == user) {
            user = new User();
            user.setOpenId(openId);
            user.setNickName(openId);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
        }
        final LeaderAddressVo leaderAddressVo = userService.getLeaderAddressVoByUserId(user.getId());

        final String name = user.getNickName();
        final String token = JwtHelper.createToken(user.getId(), name);

        final UserLoginVo userLoginVo = this.userService.getUserLoginVo(user.getId());
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(), userLoginVo, RedisConst.USERKEY_TIMEOUT, TimeUnit.DAYS);

        final Map<String, Object> map = Maps.newHashMap();
        map.put("user", user);
        map.put("leaderAddressVo", leaderAddressVo);
        map.put("token", token);

        return ResultResponse.ok(map);
    }

    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public ResultResponse<Void> updateUser(@RequestBody final User user) {
        final User user1 = userService.getById(AuthContextHolder.getUserId());
        // 把昵称更新为微信用户
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return ResultResponse.ok(null);
    }
}