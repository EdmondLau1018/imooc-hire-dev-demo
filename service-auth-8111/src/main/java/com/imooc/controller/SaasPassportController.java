package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.service.UsersService;
import com.imooc.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/saas")
public class SaasPassportController extends BaseInfoProperties {

    private final JWTUtils jwtUtils;

    private final UsersService usersService;

    public SaasPassportController(JWTUtils jwtUtils, UsersService usersService) {
        this.jwtUtils = jwtUtils;
        this.usersService = usersService;
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

    /**
     * 判断二维码是否被扫描 如果qrToken 被读取 向前端返回二维码被扫描的信息
     * 当前判断 qrToken 是否被读取的信息 由前端通过定时器发送
     *
     * @param qrToken
     * @param request
     * @return
     */
    @PostMapping("/codeHasBeenRead")
    public GraceJSONResult codeHasBeenRead(String qrToken, HttpServletRequest request) {

        //  获取 redis 中存储的 qrToken 是否被读取的信息
        String redisToken = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);
        //  用于存储返回给前端的 token 状态 和 token 内容
        List<Object> list = new ArrayList<>();

        if (StringUtils.isNotBlank(redisToken)) {

            //  因为 token 是通过逗号分割的字符串，先切分再将 读取状态 和 token 内容返回给前端
            String[] tokenArr = redisToken.split(",");
            //  判断redis 中的 token 是否符合对应的格式 （判断是否获取正确的 redis读取状态 token）
            if (tokenArr.length > 2) {
                list.add(Integer.valueOf(tokenArr[0]));      //  添加redis token 读取状态
                list.add(tokenArr[1]);          //  添加 qrToken  的值
                return GraceJSONResult.ok(list);
            } else {
                //  token 不符合预定义状态
                list.add(0);
                return GraceJSONResult.ok(list);
            }

        } else {
            //  未获取到 redis 中存储的 token 内容
            list.add(0);
            return GraceJSONResult.ok(list);
        }
    }

    @PostMapping("/goQRLogin")
    public GraceJSONResult goQRLogin(String userId, String qrToken, String preToken) {

        //  根据前端传递的 qrToken 作为  key 获取扫码后 存储在 redis 中的 preToken
        String preTokenArr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);
        if (StringUtils.isNotBlank(preTokenArr)) {

            //  截取逗号之后的内容 就是redis 中存储的 preToken 信息
            String preTokenRedis = preTokenArr.split(",")[1];
            if (preTokenRedis.equalsIgnoreCase(preToken)) {
                //  验证用户信息成功，根据用户 id 获取用户信息
                Users saasUser = usersService.getById(userId);

                //  判断当前是否获取了对应的 HR 用户
                if (saasUser == null)
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);

                //  将 获取的 hr 信息 存储在 redis 中 有效时间设置为 五分钟
                //  H5 在未登录的状态下获取不到用户信息 （如果使用 webSocket 通信则没有这种问题）
                redis.set(REDIS_SAAS_USER_INFO + ":temp:" + preToken, new Gson().toJson(saasUser), 5 * 60);
            }
        }
        return GraceJSONResult.ok();

    }

}
