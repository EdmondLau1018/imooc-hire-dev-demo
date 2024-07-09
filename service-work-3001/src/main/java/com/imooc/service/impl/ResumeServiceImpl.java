package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.mapper.ResumeMapper;
import com.imooc.mapper.ResumeWorkExpMapper;
import com.imooc.pojo.Resume;
import com.imooc.pojo.ResumeWorkExp;
import com.imooc.pojo.bo.EditResumeBO;
import com.imooc.pojo.bo.EditWorkExpBO;
import com.imooc.pojo.vo.ResumeVO;
import com.imooc.service.MqLocalMsgRecordService;
import com.imooc.service.ResumeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ResumeServiceImpl extends BaseInfoProperties implements ResumeService {

    private final ResumeMapper resumeMapper;

    private final MqLocalMsgRecordService recordService;

    private final ResumeWorkExpMapper resumeWorkExpMapper;

    public ResumeServiceImpl(ResumeMapper resumeMapper, MqLocalMsgRecordService recordService, ResumeWorkExpMapper resumeWorkExpMapper) {
        this.resumeMapper = resumeMapper;
        this.recordService = recordService;
        this.resumeWorkExpMapper = resumeWorkExpMapper;
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
        resumeMapper.update(resume, new QueryWrapper<Resume>()
                .eq("id", resume.getId())
                .eq("user_id", resume.getUserId()));

    }

    /**
     * 查询当前用户简历信息的实现方法
     *
     * @param userId
     * @return
     */
    @Override
    public ResumeVO getResumeInfo(String userId) {

        ResumeVO resumeVO = new ResumeVO();
        Resume resume = resumeMapper.selectOne(new QueryWrapper<Resume>()
                .eq("user_id", userId));
        BeanUtils.copyProperties(resume, resumeVO);
        return resumeVO;
    }

    /**
     * 修改工作经验业务实现类
     *
     * @param editWorkExpBO
     */
    @Transactional
    @Override
    public void editWorkExp(EditWorkExpBO editWorkExpBO) {

        ResumeWorkExp resumeWorkExp = new ResumeWorkExp();
        BeanUtils.copyProperties(editWorkExpBO, resumeWorkExp);

        resumeWorkExp.setUpdatedTime(LocalDateTime.now());

        //  根据 id 判断当前 工作经验是否是第一次编辑如果是 就是插入流程如果不是就是更新流程
        if (StringUtils.isNotBlank(resumeWorkExp.getId())) {

            //  执行插入流程
            resumeWorkExp.setCreateTime(LocalDateTime.now());
            resumeWorkExpMapper.insert(resumeWorkExp);
        } else {
            //  根据 工作经验 id 用户 id 简历 id 更新用户的工作经验
            resumeWorkExpMapper.update(resumeWorkExp, new QueryWrapper<ResumeWorkExp>()
                    .eq("id", resumeWorkExp.getId())
                    .eq("user_id", resumeWorkExp.getUserId())
                    .eq("resume_id", resumeWorkExp.getResumeId()));
        }

        //  删除redis 中的简历信息 等待查询的时候会自动填充
        redis.del(REDIS_RESUME_INFO + ":" + editWorkExpBO.getUserId());
    }
}
