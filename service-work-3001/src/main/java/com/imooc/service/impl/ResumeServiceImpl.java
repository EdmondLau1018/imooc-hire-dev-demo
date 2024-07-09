package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.mapper.ResumeMapper;
import com.imooc.pojo.Resume;
import com.imooc.pojo.bo.EditResumeBO;
import com.imooc.service.MqLocalMsgRecordService;
import com.imooc.service.ResumeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;

    private final MqLocalMsgRecordService recordService;

    public ResumeServiceImpl(ResumeMapper resumeMapper, MqLocalMsgRecordService recordService) {
        this.resumeMapper = resumeMapper;
        this.recordService = recordService;
    }

//    /**
//     * 初始化用户简历
//     *
//     * @param userId 用户 id
//     */
//    @Transactional      //  新增数据 加上本地事务注解
//    @Override
//    public void initResume(String userId) {
//
//        Resume resume = new Resume();
//        resume.setUserId(userId);
//        resume.setCreateTime(LocalDateTime.now());
//        resume.setUpdatedTime(LocalDateTime.now());
//
//        resumeMapper.insert(resume);
//    }

    /**
     * 初始化用户简历
     *
     * @param userId 用户 id
     */
    @Transactional      //  新增数据 加上本地事务注解
    @Override
    public void initResume(String userId, String msgId) {

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());

        resumeMapper.insert(resume);

        //  删除本地对应的消息记录表中的消息
        recordService.removeById(msgId);

    }

    /**
     * 修改用户简历实现方法
     *
     * @param editResumeBO
     */
    @Transactional
    @Override
    public void modifyResume(EditResumeBO editResumeBO) {

        Resume resume = new Resume();
        BeanUtils.copyProperties(editResumeBO, resume);

        resume.setUpdatedTime(LocalDateTime.now());

        //  根据 主键 和关联的用户 id 进行更新
        resumeMapper.update(resume,new QueryWrapper<Resume>()
                .eq("id",resume.getId())
                .eq("user_id",resume.getUserId()));

    }
}
