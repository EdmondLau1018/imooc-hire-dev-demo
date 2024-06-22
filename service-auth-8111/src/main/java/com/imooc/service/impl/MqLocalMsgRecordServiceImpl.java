package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.mapper.MqLocalMsgRecordMapper;
import com.imooc.pojo.MqLocalMsgRecord;
import com.imooc.service.MqLocalMsgRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-06-22
 */
@Service
public class MqLocalMsgRecordServiceImpl extends ServiceImpl<MqLocalMsgRecordMapper, MqLocalMsgRecord> implements MqLocalMsgRecordService {

    private final MqLocalMsgRecordMapper recordMapper;

    public MqLocalMsgRecordServiceImpl(MqLocalMsgRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Override
    public List<MqLocalMsgRecord> getBatchLocalMsgRecordList(List<String> msgIds) {
        return recordMapper.selectBatchIds(msgIds);
    }
}
