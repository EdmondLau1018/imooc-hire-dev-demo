package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.mq.RabbitMQSMSConfig;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.RegisterLoginBO;
import com.imooc.pojo.mq.SMSContentQO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/passport")
public class PassportController extends BaseInfoProperties {

    private final RedisOperator redisOperator;

    private final SMSUtils smsUtils;

    private final UsersService usersService;

    private final JWTUtils jwtUtils;

    private final RabbitTemplate rabbitTemplate;

    public PassportController(RedisOperator redisOperator, SMSUtils smsUtils, UsersService usersService, JWTUtils jwtUtils, RabbitTemplate rabbitTemplate) {
        this.redisOperator = redisOperator;
        this.smsUtils = smsUtils;
        this.usersService = usersService;
        this.jwtUtils = jwtUtils;
        this.rabbitTemplate = rabbitTemplate;
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
            //  异步解耦，向消息队列中发送 发送短信的消息
            SMSContentQO smsContentQO = new SMSContentQO();
            smsContentQO.setMobile(mobile);
            smsContentQO.setContent(code);

//            //  发送消息 到交换机 回调函数 （Confirm 机制）
//            rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//
//                /**
//                 *  MQ Server 接收到了不管是否成功这个函数都会被回调
//                 * @param correlationData   消息相关联的数据（配置信息）
//                 * @param b         交换机是否成功接收到信息 true 成功 false 失败
//                 * @param s         失败原因
//                 */
//                @Override
//                public void confirm(CorrelationData correlationData, boolean b, String s) {
//
//                    log.info("correlation_data = {}", correlationData.getId().toString());
//                    //  判断消息是否成功发送
//                    if (b) {
//                        //  ack 返回值为 true 代表消息被成功接收
//                        log.info("交换机成功收到消息：{}", s);
//                    } else {
//                        log.info("交换机接收消息失败，失败原因：{}", s);
//                    }
//                }
//            });

            //  消息从交换机路由到队列 回调函数 （Return 机制）
            rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {

                /**
                 * 消息无法路由到队列的时候才会调用这个回调函数
                 * @param returnedMessage
                 */
                @Override
                public void returnedMessage(ReturnedMessage returnedMessage) {

                    log.info("消息路由到队列的过程中出现错误，返回的信息为：{}", returnedMessage.toString());
                }
            });

            //  发送消息的时候添加参数：过期时间
//            MessagePostProcessor processor = new MessagePostProcessor() {
//
//                /**
//                 * 设置发送信息的属性
//                 * @param message
//                 * @return
//                 * @throws AmqpException
//                 */
//                @Override
//                public Message postProcessMessage(Message message) throws AmqpException {
//
//                    //  设置发送信息的属性 如果超时 10 秒钟自动丢失 ttl 为 10 秒钟
//                    message.getMessageProperties().setExpiration(String.valueOf(10 * 1000));
//                    return message;
//                }
//            };
            rabbitTemplate.convertAndSend(RabbitMQSMSConfig.SMS_EXCHANGE,
                    "com.imooc.sms.login.send",
                    GsonUtils.object2String(smsContentQO),
                    //  给发送的消息添加 CorrelationData 一般设置的是 它的 id
                    message -> {
                        //  设置 message 属性的 lambda 写法
                        message.getMessageProperties().setExpiration(String.valueOf(10 * 1000));
                        return message;
                    },
                    new CorrelationData(UUID.randomUUID().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  将生成的 随机验证码 存储到 redis 中
        //  存储到 redis 中设置 验证码有效时间为 30 分钟
        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return GraceJSONResult.ok();
    }

    /**
     * 用户手机登录接口
     *
     * @param registerLoginBO
     * @param request
     * @return
     */
    //  @Valid 注解：hibernate-validator 提供的校验 结合 BO 中通过注解设定的参数校验规则 用于判断 参数是否合法
    @PostMapping("/login")
    public GraceJSONResult login(@Valid @RequestBody RegisterLoginBO registerLoginBO, HttpServletRequest request) {
        //   当使用 RequestBody 注解时 前端传递的参数一定得是 json 格式
        String mobile = registerLoginBO.getMobile();
        String smsCode = registerLoginBO.getSmsCode();

        //  从 redis 中获取验证码 校验是否匹配
        String redisCode = redisOperator.get(MOBILE_SMSCODE + ":" + mobile);
        //  验证码不匹配 返回错误信息
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(smsCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        //  查询数据库 判断当前用户 是否存在
        Users user = usersService.queryMobileIsExist(mobile);
        //  如果查询到的用户数据为 空 表示用户没有注册过需要 创建新的用户信息入库
        if (user == null) {
            user = usersService.createUserAndInitResumeMQ(mobile);
        }

        //  查询到对应的用户 需要删除 redis 中保存的 key
        redisOperator.del(MOBILE_SMSCODE + ":" + mobile);

//        //  用户登录成功 生成token存储在 redis  中
//        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID().toString();
//        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);、

        //  改成 使z用 JWT 在客户端保存用户信息和状态 不通过服务端进行数据存储
        String uToken = jwtUtils.createJWTWithPrefix(TOKEN_USER_PREFIX,
                new Gson().toJson(user),   //   生成 JWT 携带的用户 subject 信息是 查询活创建用户的信息
                Long.valueOf(30 * 60 * 60 * 1000));     //  修改生成 JWT 的有效时长为 30 小时

        //  携带 token 信息 规避非必要信息返回的 VO 对象
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);
        //  返回用户信息
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 根据前端的 userId 删除 redis 中的 uToken
     * 实现用户退出登录的功能
     *
     * @return
     */
    @PostMapping("/logout")
    public GraceJSONResult logout(@RequestParam String userId, HttpServletRequest request) {
        // 后端：删除对应的 userToken
//        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }

}
