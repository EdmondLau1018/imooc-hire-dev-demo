package com.imooc.mq;

import com.imooc.api.mq.InitResumeMQConfig;
import com.imooc.service.ResumeService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * 简历服务消费者 监听简历队列消息
 * 成功则删除本地消息存储 若失败则消息重回队列
 */
@Slf4j
@Component
public class InitResumeConsumer {

    private final ResumeService resumeService;

    public InitResumeConsumer(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

//    @RabbitListener(queues = {InitResumeMQConfig.INIT_RESUME_QUEUE})
//    public void watchQueue(String payload, Message message) {
//
//        log.info("work-service 接收到的信息 routing-key = {}",message.getMessageProperties().getReceivedRoutingKey());
//
//        log.info("userId = {}", payload);
//        String userId = payload;
//
//        if (message.getMessageProperties().getReceivedRoutingKey().equalsIgnoreCase(InitResumeMQConfig.INIT_RESUME_ROUTING_KEY)) {
//            resumeService.initResume(userId);
//        }
//    }

    @RabbitListener(queues = {InitResumeMQConfig.INIT_RESUME_QUEUE})
    public void watchQueue(Message message, Channel channel) throws Exception {

        log.info("work-service 接收到的信息 routing-key = {}", message.getMessageProperties().getReceivedRoutingKey());

        //  获取消息内容
        String msg = new String(message.getBody());
        //  消息内容格式为 用户 id , 消息 id 所以使用逗号进行分割
        String userId = msg.split(",")[0];
        String msgId = msg.split(",")[1];

        log.info("work-service 队列消费者 ：userId = {}, messageId = {}", userId, msgId);

        try {
            //  调用 service 初始化简历信息 删除本地消息数据
            if (message.getMessageProperties().getReceivedRoutingKey().equalsIgnoreCase(InitResumeMQConfig.INIT_RESUME_ROUTING_KEY)) {
                resumeService.initResume(userId, msgId);

                //  业务执行成功 手动 ACK 确认消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                log.info("work-service 手动确认消息成功 ---  messageId = {}", msgId);
            }
        } catch (Exception e) {
            e.printStackTrace();

            //  业务流程执行失败 不确认消息 让消息重回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, false);
            log.info("work-service 手动确认消息失败 ---  messageId = {}", msgId);
        }
    }

}
