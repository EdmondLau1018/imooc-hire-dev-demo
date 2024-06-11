package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/saas")
public class SaasPassportController extends BaseInfoProperties {

    @PostMapping("/getQRToken")
    public GraceJSONResult getQrToken() {

        //  随机生成 UUID 作为token
        String qrToken = UUID.randomUUID().toString();
        //  将 token 存入 redis 并设置一定的时效
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, qrToken, 5 * 60);
        //  在 redis 中设置key 记录当前 token 是否被读取
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "0", 5 * 60);

        return GraceJSONResult.ok(qrToken);
    }
}
