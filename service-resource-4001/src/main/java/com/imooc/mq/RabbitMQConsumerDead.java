package com.imooc.mq;

import com.imooc.api.mq.RabbitMQSMSConfigDead;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQConsumerDead {


    /**
     * 死信队列消费者 监听死信队列
     */
    @RabbitListener(queues = {RabbitMQSMSConfigDead.SMS_QUEUE_DEAD})
    public void watchQueue(Message message, Channel channel) throws Exception {

        log.info("获取的路由信息为：{}",message.getMessageProperties().getReceivedRoutingKey());
        log.info("获取的消息为：{}", message.getBody());

        //  获取到 delivery 信息直接丢弃消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
