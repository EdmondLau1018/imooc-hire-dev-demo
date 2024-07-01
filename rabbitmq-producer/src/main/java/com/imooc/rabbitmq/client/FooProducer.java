package com.imooc.rabbitmq.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class FooProducer {
    public static void main(String[] args) throws IOException, TimeoutException {

        //  创建 rabbitMQ 客户端连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //  给连接工厂添加连接信息
        connectionFactory.setHost("192.168.32.100");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("com.imooc");
        connectionFactory.setPassword("com.imooc");

        //  创建连接会话 connection
        Connection connection = connectionFactory.newConnection();
        //  创建连接信道 channel
        Channel channel = connection.createChannel();
        //  新建 或者连接队列 （简单模式不使用交换机）
        /**
         * queue: 队列名称
         * durable：是否持久化，true MQ 重启时队列不会消失 ，false  MQ 重启时队列会消失
         * exclusive：是否只允许一个消费者监听这个队列 true：只允许一个消费者监听队列
         * autoDelete：在当前队列没有消费者监听的时候是否自动删除
         * arguments：创建当前队列需要的其他参数
         */
        AMQP.Queue.DeclareOk demoQueue = channel.queueDeclare("demo_queue", true, false, false, null);
        //  通过信道 channel 直接发送消息
        /**
         * exchange：交换机名称 如果没有 填 ”“
         * routingKey：路由规则，简单工作模式不需要使用 写 队列名称
         * props：配置信息
         * body: 消息数据
         */
        byte[] messageBody = "test demo_queue times 01 ".getBytes(StandardCharsets.UTF_8);
        channel.basicPublish("", "demo_queue", null, messageBody);

        //  释放资源，关闭channel 信道和connection 连接
        channel.close();
        connection.close();
    }
}
