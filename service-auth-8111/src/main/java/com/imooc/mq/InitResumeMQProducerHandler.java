package com.imooc.mq;

import com.imooc.pojo.MqLocalMsgRecord;
import com.imooc.service.MqLocalMsgRecordService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class InitResumeMQProducerHandler {

    //  将发送消息的 id 存储到 当前线程中
    private ThreadLocal<List<String>> msgIdsThreadLocal = new ThreadLocal<>();

    private final MqLocalMsgRecordService recordService;

    public InitResumeMQProducerHandler(MqLocalMsgRecordService recordService) {
        this.recordService = recordService;
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
}
