package com.imooc.mq;

import com.imooc.api.mq.InitResumeMQConfig;
import com.imooc.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitResumeConsumer {

    private final ResumeService resumeService;

    public InitResumeConsumer(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @RabbitListener(queues = {InitResumeMQConfig.INIT_RESUME_QUEUE})
    public void watchQueue(String payload, Message message) {

        log.info("work-service 接收到的信息 routing-key = {}",message.getMessageProperties().getReceivedRoutingKey());

        log.info("userId = {}", payload);
        String userId = payload;

        if (message.getMessageProperties().getReceivedRoutingKey().equalsIgnoreCase(InitResumeMQConfig.INIT_RESUME_ROUTING_KEY)) {
            resumeService.initResume(userId);
        }
    }

}
