package com.imooc.rabbitmq.client;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class TopicProducer {
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

        //  新建交换机 选择 DIRECT 广播类型
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
        String topicExchange = "topic_exchange";
        channel.exchangeDeclare(topicExchange, BuiltinExchangeType.TOPIC, true, false, false, null);

        //  新建队列
        String topicQueueOrder = "topic_queue_order";
        channel.queueDeclare(topicQueueOrder, true, false, false, null);
        String topicQueuePay = "topic_queue_pay";
        channel.queueDeclare(topicQueuePay, true, false, false, null);

        //  绑定 队列和交换机（通过信道 channel 进行绑定）
        //  参数1：队列名称 参数2：交换机名称 参数3：routing-key
        channel.queueBind(topicQueueOrder, topicExchange, "order.*");
        channel.queueBind(topicQueuePay, topicExchange, "#.pay.*");

        //  通过信道 channel 直接发送消息
        String msg1 = "创建订单A";
        String msg2 = "创建订单B";
        String msg3 = "修改订单C";
        String msg4 = "修改订单D";
        String msg5 = "支付订单E";
        String msg6 = "支付订单F";

        //  将不同消息 通过channel 不同的 routing-key 发送出去
        //  exchange, routingKey, mandatory, immediate, props, body
        channel.basicPublish(topicExchange, "order.create", null, msg1.getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(topicExchange, "order.create", null, msg2.getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(topicExchange, "order.update", null, msg3.getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(topicExchange, "order.update", null, msg4.getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(topicExchange, "imooc.pay.order", null, msg5.getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(topicExchange, "imooc.pay.order", null, msg6.getBytes(StandardCharsets.UTF_8));


        //  释放资源，关闭channel 信道和connection 连接
        channel.close();
        connection.close();
    }
}
