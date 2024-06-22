package com.imooc.mq;

import com.imooc.pojo.MqLocalMsgRecord;
import com.imooc.service.MqLocalMsgRecordService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class InitResumeMQProducerHandler {

    //  将发送消息的 id 存储到 当前线程中
    private ThreadLocal<List<String>> msgIdsThreadLocal = new ThreadLocal<>();

    private final MqLocalMsgRecordService recordService;

    private final RabbitTemplate rabbitTemplate;

    public InitResumeMQProducerHandler(MqLocalMsgRecordService recordService, RabbitTemplate rabbitTemplate) {
        this.recordService = recordService;
        this.rabbitTemplate = rabbitTemplate;
    }


    /**
     * 保存消息到本地
     * @param targetExchange
     * @param routingKey
     * @param msgContent
     */
    public void saveLocalMsg(String targetExchange, String routingKey, String msgContent) {

        MqLocalMsgRecord record = new MqLocalMsgRecord();
        record.setTargetExchange(targetExchange);
        record.setRoutingKey(routingKey);
        record.setMsgContent(msgContent);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        recordService.save(record);

        //  从 ThreadLocal 中获取当前消息列表 如果为空 则初始化
        List<String> msgIds = msgIdsThreadLocal.get();
        if (CollectionUtils.isEmpty(msgIds)) {
            //  初始化新的列表并添加到 ThreadLocal 对象中
            msgIds = new ArrayList<String>();
        }
        //  将保存的消息 id 添加到列表中
        msgIds.add(record.getId());
        //  将列表信息添加到 threadLocal 中
        msgIdsThreadLocal.set(msgIds);

    }

    /**
     * 查询本地所有的消息，对未发送的消息进行发送
     */
    public void sendAllLocalMsg(){

        //  从 ThreadLocal 中获取 本地消息 id 列表
        List<String> msgIds = msgIdsThreadLocal.get();

        if (CollectionUtils.isEmpty(msgIds)) {
            //  当前 消息id 列表为空 说明并没有消息需要发送
            return;
        }
        //  根据 当前线程中的消息 id 查询出本地存储的消息
        List<MqLocalMsgRecord> msgRecordList = recordService.getBatchLocalMsgRecordList(msgIds);
        //  循环本地消息列表 对这些消息进行逐一发送
        for (MqLocalMsgRecord msgRecord : msgRecordList) {
            //  随机生成消息关联 id
            rabbitTemplate.convertAndSend(msgRecord.getTargetExchange(),
                    msgRecord.getRoutingKey(),
                    msgRecord.getMsgContent(),
                    new CorrelationData(UUID.randomUUID().toString()));
        }
    }
}
