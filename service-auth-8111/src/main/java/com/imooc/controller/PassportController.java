package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
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
        //   生成验证码  (随机验证码为 六位数 转 String)
        String code = (int) (Math.random() * 9 + 1) * 100000 + "";
        log.info("生成的随机验证码为：{}", code);
        //  发送验证码到用户的手机上
        try {
            smsUtils.sendSMS(mobile, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  将生成的 随机验证码 存储到 redis 中
        //  存储到 redis 中设置 验证码有效时间为 30 分钟
        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return GraceJSONResult.ok();
    }
}
