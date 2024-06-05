package com.imooc;

import com.google.gson.Gson;
import com.imooc.test.pojo.Stu;
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
                setSubject(stuJson).
                signWith(secretKey).
                compact();

        System.out.println(testJwt);
    }
}
