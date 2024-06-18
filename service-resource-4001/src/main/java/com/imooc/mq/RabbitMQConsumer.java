package com.imooc.mq;

import com.imooc.api.mq.RabbitMQSMSConfig;
import com.imooc.pojo.mq.SMSContentQO;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQConsumer {

    private final SMSUtils smsUtils;

    public RabbitMQConsumer(SMSUtils smsUtils) {
        this.smsUtils = smsUtils;
    }

    /**
     * 消费者监听队列的方法
     *
     * @param payLoad
     * @param message
     */
    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE}) //  这个注解的参数是一个数组表示可以监听多个队列
    public void watchQueue(String payLoad, Message message) throws Exception {

        // 这里的 payLoad 记录的就是消息的内容
        log.info("payLoad = {}", payLoad);

        //  获取消息的路由信息
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routing key = {}", routingKey);
        if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.SMS_ROUTING_KEY_LOGIN)) {
            //  路由验证正确，调用发送短信的工具类发送短信
            String msg = payLoad;
            // 类型转换
            SMSContentQO smsContentQO = GsonUtils.stringToBean(msg, SMSContentQO.class);
            smsUtils.sendSMS(smsContentQO.getMobile(), smsContentQO.getContent());
        }
    }
}
