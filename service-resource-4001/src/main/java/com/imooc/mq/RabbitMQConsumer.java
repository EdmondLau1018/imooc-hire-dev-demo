package com.imooc.mq;

import com.imooc.api.mq.RabbitMQSMSConfig;
import com.imooc.pojo.mq.SMSContentQO;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.SMSUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
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
//    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE}) //  这个注解的参数是一个数组表示可以监听多个队列
//    public void watchQueue(String payLoad, Message message) throws Exception {
//
//        // 这里的 payLoad 记录的就是消息的内容
//        log.info("payLoad = {}", payLoad);
//
//        //  获取消息的路由信息
//        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
//        log.info("routing key = {}", routingKey);
//        if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.SMS_ROUTING_KEY_LOGIN)) {
//            //  路由验证正确，调用发送短信的工具类发送短信
//            String msg = payLoad;
//            // 类型转换
//            SMSContentQO smsContentQO = GsonUtils.stringToBean(msg, SMSContentQO.class);
//            smsUtils.sendSMS(smsContentQO.getMobile(), smsContentQO.getContent());
//        }
//    }

    /**
     * 手动确认 消息的 ack 方法
     *
     * @param message
     * @param channel 这里用的是 RabbitMQ 的 channel 用于手动确认信息
     * @throws Exception
     */
    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE})
    public void watchQueue(Message message, Channel channel) throws Exception {

        try {
            // 这里的 payLoad 记录的就是消息的内容
            String messageBody = new String(message.getBody());
            log.info("信息内容 = {}", messageBody);

            //  获取消息的路由信息
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            log.info("routing key = {}", routingKey);
            if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.SMS_ROUTING_KEY_LOGIN)) {
                //  路由验证正确，调用发送短信的工具类发送短信
                String msg = messageBody;
                // 类型转换
                SMSContentQO smsContentQO = GsonUtils.stringToBean(msg, SMSContentQO.class);
//                smsUtils.sendSMS(smsContentQO.getMobile(), smsContentQO.getContent());
                log.info("模拟发送短信的业务逻辑，发送到的手机号为：{}，验证码为：{}",smsContentQO.getMobile(),smsContentQO.getContent());

                // 手动确认信息
                /**
                 * deliveryTag: 消息投递的标签
                 * multiple：是否批量确认消费的消息（true 确认，false 不确认）
                 */
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            }
        } catch (Exception e) {

            //  如果出现了代码或者业务异常可以通过 nack 不确认消息
            /**
             * deliveryTag,
             * multiple,
             * requeue : 是否将未确认的消息重新放回队列 (true，放回队列，false：丢弃消息)
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            e.printStackTrace();
        }
    }
}
