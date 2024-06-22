package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.MqLocalMsgRecord;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-06-22
 */
public interface MqLocalMsgRecordService extends IService<MqLocalMsgRecord> {

    /**
     * 根据 本地消息 id 列表查询所有的本地消息对象 （mq_local_msg_record ）表
     * @param msgIds
     * @return
     */
    public List<MqLocalMsgRecord> getBatchLocalMsgRecordList(List<String> msgIds);

}
