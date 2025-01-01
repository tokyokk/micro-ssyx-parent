package com.micro.ssyx.common.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/5/26 17:07
 */
public class JwtHelper {

    private static final long TOKEN_EXPIRATION = 365L * 24 * 60 * 60 * 1000;
    private static final String TOKEN_SIGN_KEY = "ssyx";

    public static String createToken(final Long userId, final String userName) {
        final String token = Jwts.builder()
                .setSubject("ssyx-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SIGN_KEY)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Long getUserId(final String token) {
        if (StringUtils.isEmpty(token)) return null;

        final Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
        final Claims claims = claimsJws.getBody();
        final Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
        // return 1L;
    }

    public static String getUserName(final String token) {
        if (StringUtils.isEmpty(token)) return "";

        final Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
        final Claims claims = claimsJws.getBody();
        return (String) claims.get("userName");
    }

    public static void removeToken(final String token) {
        // jwttoken无需删除，客户端扔掉即可。
    }

    public static void main(final String[] args) {
        final String token = JwtHelper.createToken(7L, "admin");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }
}
