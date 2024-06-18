package com.imooc.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class PubSubProducer {
    public static void main(String[] args) throws IOException, TimeoutException {

        //  创建 rabbitMQ 客户端连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //  给连接工厂添加连接信息
        connectionFactory.setHost("192.168.32.100");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("imooc");
        connectionFactory.setPassword("imooc");

        //  创建连接会话 connection
        Connection connection = connectionFactory.newConnection();
        //  创建连接信道 channel
        Channel channel = connection.createChannel();

        //  新建交换机 选择 FANOUT 广播类型
        /**
         * String exchange 交换机名称
         * String type 枚举类 交换机类型
         *     DIRECT("direct"),  根据 routing-key 匹配队列
         *     FANOUT("fanout"),  所有绑定的队列都能收到消息
         *     TOPIC("topic"),    符合 routing-pattern 的队列可以收到消息
         *     HEADERS("headers"); 参数匹配 不常用
         * boolean durable 是否持久化
         * boolean autoDelete 是否自动删除
         * boolean internal 是否是 rabbitMQ 内部使用的交换机 默认 false
         *  Map<String, Object> arguments 其他参数
         *
         */
        String fanoutExchange = "fanout_exchange";
        channel.exchangeDeclare("fanout_exchange", BuiltinExchangeType.FANOUT, true, false, false, null);

        //  新建队列
        String fanoutQueueA = "fanout_queue_a";
        channel.queueDeclare("fanout_queue_a", true, false, false, null);
        String fanoutQueueB = "fanout_queue_b";
        channel.queueDeclare("fanout_queue_b", true, false, false, null);

        //  绑定 队列和交换机（通过信道 channel 进行绑定）
        //  参数1：队列名称 参数2：交换机名称 参数3：routing-key
        channel.queueBind(fanoutQueueA, fanoutExchange, "");
        channel.queueBind(fanoutQueueB, fanoutExchange, "");

        //  通过信道 channel 直接发送消息
        for (int i = 0; i < 30; i++) {
            String messageBody = "fanout_queue test times + [ " + i + " ]";
            //  String exchange, String routingKey, BasicProperties props, byte[] body
            channel.basicPublish(fanoutExchange, "", null, messageBody.getBytes(StandardCharsets.UTF_8));
        }
        //  释放资源，关闭channel 信道和connection 连接
        channel.close();
        connection.close();
    }
}
