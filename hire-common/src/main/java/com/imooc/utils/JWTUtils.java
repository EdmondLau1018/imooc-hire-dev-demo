package com.imooc.utils;

import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RefreshScope       //  注解：从配置中心获取配置 动态扫描配置内容 配置中心的配置更改后这个也会更改
@Slf4j
@Component
public class JWTUtils {

    private static final String SYMBOL_AT = "@";

    private final JWTResource jwtResource;

    @Value("${jwt.key}")
    public String JWT_KEY;

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
     *
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
//        String base64 = new BASE64Encoder().encode(jwtResource.getKey().getBytes(StandardCharsets.UTF_8));
        //  将本地保存的 jwt 密钥 修改为 nacos 配置中心保存的密钥
        String base64 = new BASE64Encoder().encode(JWT_KEY.getBytes(StandardCharsets.UTF_8));
        log.info("生成 jwt --- 从 配置中心中获取的 jwt 密钥信息： {}", JWT_KEY);

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

    /**
     * jwt 校验方法 如果校验成功 则返回解析出的用户信息 json
     *
     * @param jwtString
     * @return
     */
    public String checkJWT(String jwtString) {
        //  对密钥进行 base64 编码
//        String base64 = new BASE64Encoder().encode(jwtResource.getKey().getBytes(StandardCharsets.UTF_8));
        String base64 = new BASE64Encoder().encode(JWT_KEY.getBytes(StandardCharsets.UTF_8));
        log.info("校验 jwt --- 从 配置中心中获取的 jwt 密钥信息： {}", JWT_KEY);
        //  生成密钥对象 自动选择加密算法
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes(StandardCharsets.UTF_8));
        //  使用 Jwts 校验 密钥对象
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        //  解析 jwt 对象
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwtString);
        //  如果解析成功 从 Claims 对象中可以获得 subject 用户信息
        String subject = claimsJws.getBody().getSubject();
        return subject;
    }

}
