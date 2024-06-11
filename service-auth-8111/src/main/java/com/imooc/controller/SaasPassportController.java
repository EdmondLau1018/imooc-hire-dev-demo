package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/saas")
public class SaasPassportController extends BaseInfoProperties {

    private final JWTUtils jwtUtils;

    public SaasPassportController(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * SAAS web 端 扫描二维码获取 QR Token
     *
     * @return
     */
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

    @PostMapping("/scanCode")
    public GraceJSONResult scanCode(String qrToken, HttpServletRequest request) {

        //  token 判空 （用户扫码请求携带的 token 用户扫码就会发送这个请求 这个token 是用户扫码请求携带的参数）
        if (StringUtils.isBlank(qrToken))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);

        //  从用户携带的请求 header 中获取 userToken  (这里的 token 指的是 用户登录应用之后获取的 jwt—token 保存的用户信息)
        //  获取请求 header 中 携带的用户主键
        String appUserId = request.getHeader("appUserId");
        //  获取请求header中携带的用户身份token（jwt）
        String appUserToken = request.getHeader("appUserToken");

        //  jwt 用户身份token 校验
        if (StringUtils.isBlank(appUserToken))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        //  校验 jwt 判断用户是否登录
        String userJson = jwtUtils.checkJWT(appUserToken.split("@")[1]);

        //  如果 jwt 校验成功 会返回 当前用户的 json 字符串信息
        if (StringUtils.isBlank(userJson))
            return GraceJSONResult.exception(ResponseStatusEnum.HR_TICKET_INVALID);

        //  没有异常抛出 证明 用户校验正常，生成 preToken 替换 redis 中保存的 qrToken
        //  返回给前端 让前端在下一次请求中携带 preToken
        String preToken = UUID.randomUUID().toString();
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, preToken, 5 * 60);
        //  写入标记 表示 qrToken 已经被读取
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "1," + preToken, 5 * 60);

        return GraceJSONResult.ok(preToken);
    }

}
