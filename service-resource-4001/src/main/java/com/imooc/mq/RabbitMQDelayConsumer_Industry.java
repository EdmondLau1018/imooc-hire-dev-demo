package com.imooc.mq;

import com.imooc.api.mq.DelayConfig_Industry;
import com.imooc.api.mq.RabbitMQSMSConfigDead;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQDelayConsumer_Industry {


    /**
     * 死信队列消费者 监听死信队列
     */
    @RabbitListener(queues = {DelayConfig_Industry.QUEUE_DELAY_REFRESH})
    public void watchQueue(Message message, Channel channel) throws Exception {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        log.info("获取的路由信息为：{}",routingKey);
        log.info("获取的消息为：{}", new String(message.getBody()));

        //  检查 routing_key 是否符合拟定的 路由规则 如果符合执行 缓存更新业务
        if (routingKey.equalsIgnoreCase(DelayConfig_Industry.DELAY_REFRESH_INDUSTRY)) {
            log.info("监听延迟队列消息 执行业务~~~");
        }

        //  获取到 delivery 信息直接丢弃消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
