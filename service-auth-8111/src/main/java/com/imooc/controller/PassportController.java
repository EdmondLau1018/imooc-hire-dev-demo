package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import com.imooc.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/passport")
public class PassportController extends BaseInfoProperties {

    private final RedisOperator redisOperator;

    private final SMSUtils smsUtils;

    public PassportController(RedisOperator redisOperator, SMSUtils smsUtils) {
        this.redisOperator = redisOperator;
        this.smsUtils = smsUtils;
    }

    @PostMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        //  检查参数是否为空
        //  使用 apache 的String工具 isBlank 方法不仅可以判断当前 字符串是否为空 而且还可以判断空格
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }
        //  限制当前用户请求这个接口的时间 相当于是 利用 redis 完成 一种锁的机制
        //  获取用户 ip （获取的是真实 ip 越过反向代理和网关）
        String userIp = IPUtil.getRequestIp(request);
        //  将 含有 用户 ip 的信息作为key 存储在 redis 中 如果用户在 某个时间段内重新发送请求
        //  通过拦截器判断当前 ip 是否在redis 中存在
        redisOperator.setnx60s(MOBILE_SMSCODE + ":" + userIp, mobile);
        //   生成验证码  (随机验证码为 六位数 转 String)
        String code = (int) (Math.random() * 900000) + 100000 + "";
        log.info("生成的随机验证码为：{}", code);
        //  发送验证码到用户的手机上
        try {
//            smsUtils.sendSMS(mobile, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  将生成的 随机验证码 存储到 redis 中
        //  存储到 redis 中设置 验证码有效时间为 30 分钟
        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return GraceJSONResult.ok();
    }
}
