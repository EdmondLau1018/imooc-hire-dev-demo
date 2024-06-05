package com.imooc;

import com.google.gson.Gson;
import com.imooc.test.pojo.Stu;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class JJWTTest {

    //  定义 JWT 加密密钥
    public static final String USER_KEY = "Imooc_Sharn_1018_testuserkey@@@_complex_keys";

    @Test
    public void testJJWT() {

        //  对 密钥进行 base64 编码
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes(StandardCharsets.UTF_8));
        //  根据 编码后的 base64 生成一个密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes(StandardCharsets.UTF_8));
        //  用户个人信息 json 字符串
        Stu stu = new Stu(1001, "imooc", "female");
        String stuJson = new Gson().toJson(stu);
        //  生成 JWT 加密后的字符串
        String testJwt = Jwts.builder().
                setSubject(stuJson).        //  用户自定义的信息
                signWith(secretKey).        //  使用加密的密钥进行签名
                compact();

        System.out.println(testJwt);
    }

    @Test
    public void checkJWT(){
        String jwtString = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ7XCJpZFwiOjEwMDEsXCJuYW1lXCI6XCJpbW9vY1wiLFwic2V4XCI6XCJmZW1hbGVcIn0ifQ.F_1zsp6lU4nwWR5MWRsD-MXbEzhpq7U-xhha35CbBIUsBIxZ3Jxeh2nFVe5of68y";

        //  对密钥进行 base64 编码
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes(StandardCharsets.UTF_8));
        //  生成密钥对象 自动选择加密算法
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes(StandardCharsets.UTF_8));
        //  使用 Jwts 校验 密钥对象
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        //  解析 jwt 对象
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwtString);
        //  如果解析成功 从 Claims 对象中可以获得 subject 用户信息
        String subject = claimsJws.getBody().getSubject();
        //  将 subject 解析成对象
        Stu stu = new Gson().fromJson(subject, Stu.class);

        System.out.println(stu);
    }
}
