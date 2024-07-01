package com.imooc.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RoutingConsumerOrder {
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
        AMQP.Queue.DeclareOk routingQueueOrder = channel.queueDeclare("routing_queue_order", true, false, false, null);

        //  创建消费者监听队列
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            /**
             * 监听消息内容的方法 ：监听队列获取消息内容
             * @param consumerTag   消息标签（标识）
             * @param envelope      信封（记录交换机和路由信息的参数）
             * @param properties    配置信息 与生产者配置的一致
             * @param body          消息数据
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                System.out.println("routing_queue_order body  =  " + new String(body));
            }
        };

        //  信道 创建消费者 绑定队列和消费者的关系 监听队列消费消息
        channel.basicConsume("routing_queue_order", true, defaultConsumer);

        //  不关闭资源 保持监听 ...

    }
}
