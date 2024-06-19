package com.imooc.api.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建交换机 队列 绑定关系
 * 这里的三者 返回类型都在 springframework.amqp.core 包中
 * 不要用 rabbitmq.client 包里的
 */
@Configuration
public class RabbitMQSMSConfigDead {

    public static final String SMS_EXCHANGE_DEAD = "sms_exchange_dead";

    public static final String SMS_QUEUE_DEAD = "sms_queue_dead";

    public static final String SMS_ROUTING_KEY_LOGIN_DEAD = "dead.sms.display";

    /**
     * 死信队列交换机
     * @return
     */
    @Bean(SMS_EXCHANGE_DEAD)
    public Exchange exchangeDead() {
        return ExchangeBuilder
                .topicExchange(SMS_EXCHANGE_DEAD)
                .durable(true)
                .build();
    }

    /**
     * 创建死信队列
     * @return
     */
    @Bean(SMS_QUEUE_DEAD)
    public Queue queueDead() {
        return QueueBuilder
                .durable(SMS_QUEUE_DEAD)
                .build();
    }

    /**
     * 创建绑定关系
     * @return
     */
    @Bean
    public Binding smsDeadBinding(@Qualifier(SMS_EXCHANGE_DEAD) Exchange exchange,
                                  @Qualifier(SMS_QUEUE_DEAD) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("dead.sms.*")
                .noargs();
    }

}
