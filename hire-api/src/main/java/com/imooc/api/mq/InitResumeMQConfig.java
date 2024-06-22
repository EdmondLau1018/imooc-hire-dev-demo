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
public class InitResumeMQConfig {

    //  定义交换机名称
    public static final String INIT_RESUME_EXCHANGE = "init_resume_exchange";

    //  定义队列名称
    public static final String INIT_RESUME_QUEUE = "init_resume_queue";

    public static final String INIT_RESUME_ROUTING_KEY = "init.resume.display";

    /**
     * 定义交换机 将交换机注入到 spring 容器中
     *
     * @return
     */
    @Bean(INIT_RESUME_EXCHANGE)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(INIT_RESUME_EXCHANGE)
                .durable(true).build();
    }

    /**
     * 定义队列，将队列放在 spring 容器中
     *
     * @return
     */
    @Bean(INIT_RESUME_QUEUE)
    public Queue queue() {
        return QueueBuilder
                .durable(INIT_RESUME_QUEUE)
                //  新建队列的时候添加参数 消息过期时间
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
    public Binding initResumeBinding(@Qualifier(INIT_RESUME_EXCHANGE) Exchange exchange,
                              @Qualifier(INIT_RESUME_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("init.resume.#").noargs();
    }
}
