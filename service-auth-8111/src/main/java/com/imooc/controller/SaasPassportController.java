package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.SaasUserVO;
import com.imooc.service.UsersService;
import com.imooc.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * app 用户点击 确认登陆SaaS 按钮 接口
     * 根据用户传递的 token 和 preToken 找到 存储在 redis 中的用户信息
     * 将登录状态的 HR 用户信息临时存放在 redis 中
     *
     * @param userId
     * @param qrToken
     * @param preToken
     * @return
     */
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

    /**
     * 前端定时发送的请求 检查用户的登陆状态 （SAAS 页面自动刷新）
     * 根据 preToken 在 redis 中寻找 用户登录接口的 临时用户信息
     * 将临时用户信息转存为 不过期的用户信息
     * 生成新的 jwtToken （SaaS） 返回给前端
     *
     * @param preToken
     * @return
     */
    @PostMapping("/checkLogin")
    public GraceJSONResult checkLogin(String preToken) {

        //  获取判断前端参数中的 preToken 信息
        if (StringUtils.isBlank(preToken))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        //  通过前端 preToken 组成的 key 查询 redis 获取临时用户信息
        String userJson = redis.get(REDIS_SAAS_USER_INFO + ":temp:" + preToken);

        // 根据临时用户信息生成新的 jwtToken 并且长期有效
        String saasUserToken = jwtUtils.createJWTWithPrefix(TOKEN_SAAS_PREFIX, userJson, Long.valueOf(8 * 60 * 60 * 1000));
        //  将当前登录的用户信息存储在 redis 中 设置长期有效
        redis.set(REDIS_SAAS_USER_INFO + ":" + saasUserToken, userJson);

        //  将根据登录用户信息生成的新的 saasUserToken (jwt) 返回给前端
        return GraceJSONResult.ok(saasUserToken);
    }

    /**
     * 获取用户的基本信息 并且展示在前端
     * 没有 websocket 前端通过定时器发送的请求
     *
     * @param token
     * @return
     */
    @GetMapping("/info")
    public GraceJSONResult info(String token) {

        //  从 redis 中 根据 传递的 jwtToken 查询到登录用户信息
        String saasUserToken = token;
        String saasUserJson = redis.get(REDIS_SAAS_USER_INFO + ":" + saasUserToken);

        //  将查询出的 用户信息 json 转换成对象 vo 返回给前端 （通过 vo 关键信息脱敏）
        Users saasUser = new Gson().fromJson(saasUserJson, Users.class);
        SaasUserVO saasUserVO = new SaasUserVO();
        BeanUtils.copyProperties(saasUser, saasUserVO);

        return GraceJSONResult.ok(saasUserVO);

    }

    /**
     * 用户退出接口 用于后续拓展
     *
     * @return
     */
    @PostMapping("/logout")
    public GraceJSONResult logout() {

        return GraceJSONResult.ok();
    }

}
