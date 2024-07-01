package com.imooc.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class WorkQueueConsumerB {
    public static void main(String[] args) throws IOException, TimeoutException {

        //  新建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //  连接信息
        factory.setHost("192.168.32.100");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("com.imooc");
        factory.setPassword("com.imooc");

        //  创建 connection 会话
        Connection connection = factory.newConnection();

        //  创建 channel 信道
        Channel channel = connection.createChannel();
        //  创建队列
        AMQP.Queue.DeclareOk workQueue = channel.queueDeclare("work_queue", true, false, false, null);

        //  创建消费者监听队列
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("work_queue_b body  =  " + new String(body));
            }
        };

        //  信道 创建消费者 绑定队列和消费者的关系 监听队列消费消息
        channel.basicConsume("work_queue", true, defaultConsumer);

        //  不关闭资源 保持监听 ...

    }
}
