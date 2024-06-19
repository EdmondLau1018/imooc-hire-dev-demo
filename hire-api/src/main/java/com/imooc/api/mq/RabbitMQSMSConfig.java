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
public class RabbitMQSMSConfig {

    //  定义交换机名称
    public static final String SMS_EXCHANGE = "sms_exchange";

    //  定义队列名称
    public static final String SMS_QUEUE = "sms_queue";

    public static final String SMS_ROUTING_KEY_LOGIN = "imooc.sms.login.send";

    /**
     * 定义交换机 将交换机注入到 spring 容器中
     *
     * @return
     */
    @Bean(SMS_EXCHANGE)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(SMS_EXCHANGE)
                .durable(true).build();
    }

    /**
     * 定义队列，将队列放在 spring 容器中
     *
     * @return
     */
    @Bean(SMS_QUEUE)
    public Queue queue() {
        return QueueBuilder
                .durable(SMS_QUEUE)
                //  新建队列的时候添加参数 消息过期时间
                .withArgument("x-message-ttl", 10 * 1000)
                .withArgument("x-dead-letter-exchange",RabbitMQSMSConfigDead.SMS_EXCHANGE_DEAD)
                .withArgument("x-dead-letter-routing-key",RabbitMQSMSConfigDead.SMS_ROUTING_KEY_LOGIN_DEAD)
                .build();
    }

    /**
     * 创建 队列和交换机的绑定关系
     *
     * @param exchange
     * @param queue
     * @return
     */
    @Bean
    public Binding smsBinding(@Qualifier(SMS_EXCHANGE) Exchange exchange,
                              @Qualifier(SMS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("imooc.sms.#").noargs();
    }
}
