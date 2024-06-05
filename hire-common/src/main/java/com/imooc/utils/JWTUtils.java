package com.imooc.utils;

import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtils {

    private static final String SYMBOL_AT = "@";

    private final JWTResource jwtResource;

    public JWTUtils(JWTResource jwtResource) {
        this.jwtResource = jwtResource;
    }

    /**
     * 生成 带有前缀的 JWT (含过期时间)
     *
     * @param body
     * @param expireTimes
     * @return
     */
    public String createJWTWithPrefix(String prefix, String body, Long expireTimes) {
        if (expireTimes == null)
            //  校验调用方传递的 过期时间是否为 null 如果是 null 则直接抛出异常
            GraceException.displayException(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        return prefix + SYMBOL_AT + dealJWT(body, expireTimes);
    }

    /**
     * 生成带有前缀的 JWT （不含过期时间）
     * @param prefix
     * @param body
     * @return
     */
    public String createJWTWithPrefix(String prefix, String body) {
        return prefix + SYMBOL_AT + dealJWT(body, null);
    }

    /**
     * 调用这个方法生成 JWT
     *
     * @param body
     * @param expireTimes
     * @return
     */
    public String createJWT(String body, Long expireTimes) {
        if (expireTimes == null)
            //  校验调用方传递的 过期时间是否为 null 如果是 null 则直接抛出异常
            GraceException.displayException(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        return dealJWT(body, expireTimes);
    }

    /**
     * 方法重载 ，生成 JWT 且没有 过期时间参数
     *
     * @param body
     * @return
     */
    public String createJWT(String body) {
        return dealJWT(body, null);
    }

    public String dealJWT(String body, Long expireTimes) {

        //  对 密钥进行 base64 编码
        String base64 = new BASE64Encoder().encode(jwtResource.getKey().getBytes(StandardCharsets.UTF_8));
        //  根据 编码后的 base64 生成一个密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes(StandardCharsets.UTF_8));
        String jwt = "";
        // 根据调用方是否传 过期时间参数决定调用 哪个生成 JWT 的方法
        if (expireTimes == null) {
            jwt = generateJWT(body, secretKey);
        } else {
            jwt = generateJWT(body, expireTimes, secretKey);
        }
        //  调用 生成 JWT 的工具方法
        return jwt;

    }

    /**
     * 生成无需过期时间的 JWT 加密字符串
     *
     * @param body
     * @param secretKey
     * @return
     */
    public String generateJWT(String body, SecretKey secretKey) {
        //  生成 JWT 加密后的字符串
        return Jwts.builder().setSubject(body).        //  用户自定义的信息
                signWith(secretKey).        //  使用加密的密钥进行签名
                compact();
    }

    /**
     * 方法重载：生成带有有效时间的 JWT
     *
     * @param body
     * @param expireTimes
     * @param secretKey
     * @return
     */
    public String generateJWT(String body, Long expireTimes, SecretKey secretKey) {
        //   为有效时长 单位是 毫秒
        Date expireDate = new Date(System.currentTimeMillis() + expireTimes);
        return Jwts.builder().
                setExpiration(expireDate).
                setSubject(body).
                signWith(secretKey).
                compact();
    }

}
