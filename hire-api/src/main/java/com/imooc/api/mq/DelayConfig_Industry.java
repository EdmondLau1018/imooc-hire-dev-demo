package com.imooc.api.mq;

import org.springframework.amqp.AmqpException;
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
public class DelayConfig_Industry {

    //  定义交换机名称
    public static final String EXCHANGE_DELAY_REFRESH = "exchange_delay_refresh";

    //  定义队列名称
    public static final String QUEUE_DELAY_REFRESH = "queue_delay_refresh";

    //  延时队列路由
    public static final String DELAY_REFRESH_INDUSTRY = "delay.refresh.industry";

    /**
     * 定义交换机 将交换机注入到 spring 容器中
     *
     * @return
     */
    @Bean(EXCHANGE_DELAY_REFRESH)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_DELAY_REFRESH)
                .durable(true)
                .delayed()      //  将交换机声明为支持延迟队列的交换机
                .build();
    }

    /**
     * 定义队列，将队列放在 spring 容器中
     *
     * @return
     */
    @Bean(QUEUE_DELAY_REFRESH)
    public Queue queue() {
        return QueueBuilder
                .durable(QUEUE_DELAY_REFRESH)
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
    public Binding delayBindingIndustry(@Qualifier(EXCHANGE_DELAY_REFRESH) Exchange exchange,
                              @Qualifier(QUEUE_DELAY_REFRESH) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("delay.refresh.*")
                .noargs();
    }

    /**
     *  设置消息属性处理器 ，用于设置消息的发送方式和延迟时间
     * @param times
     * @return
     */
    public static MessagePostProcessor setDelayedTimes(Integer times){

        //  返回一个重新定义的 消息发送处理器
        return new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws AmqpException {

                //  设置消息的发送方式，持久化
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                //  设置消息的延迟事件
                message.getMessageProperties().setDelay(times);

                return message;
            }
        };
    }
}
