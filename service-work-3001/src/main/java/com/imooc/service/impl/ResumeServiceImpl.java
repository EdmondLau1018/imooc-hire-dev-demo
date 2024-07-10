package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.*;
import com.imooc.pojo.vo.ResumeVO;
import com.imooc.service.MqLocalMsgRecordService;
import com.imooc.service.ResumeService;
import com.imooc.utils.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeServiceImpl extends BaseInfoProperties implements ResumeService {

    private final ResumeMapper resumeMapper;

    private final MqLocalMsgRecordService recordService;

    private final ResumeWorkExpMapper resumeWorkExpMapper;

    private final ResumeProjectExpMapper resumeProjectExpMapper;

    private final ResumeEducationMapper resumeEducationMapper;

    private final ResumeExpectMapper resumeExpectMapper;

    public ResumeServiceImpl(ResumeMapper resumeMapper, MqLocalMsgRecordService recordService, ResumeWorkExpMapper resumeWorkExpMapper, ResumeProjectExpMapper resumeProjectExpMapper, ResumeEducationMapper resumeEducationMapper, ResumeExpectMapper resumeExpectMapper) {
        this.resumeMapper = resumeMapper;
        this.recordService = recordService;
        this.resumeWorkExpMapper = resumeWorkExpMapper;
        this.resumeProjectExpMapper = resumeProjectExpMapper;
        this.resumeEducationMapper = resumeEducationMapper;
        this.resumeExpectMapper = resumeExpectMapper;
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

        //  查询简历信息
        Resume resume = resumeMapper.selectOne(new QueryWrapper<Resume>()
                .eq("user_id", userId));
        BeanUtils.copyProperties(resume, resumeVO);

        //  查询工作经验信息
        List<ResumeWorkExp> resumeWorkExpList = resumeWorkExpMapper.selectList(new QueryWrapper<ResumeWorkExp>()
                .eq("user_id", userId)
                .eq("resume_id", resume.getId())
                .orderByDesc("begin_date"));

        //  查询项目经验信息
        List<ResumeProjectExp> resumeProjectExpList = resumeProjectExpMapper.selectList(new QueryWrapper<ResumeProjectExp>()
                .eq("user_id", userId)
                .eq("resume_id", resume.getId())
                .orderByDesc("begin_date"));

        //  查询教育经历
        List<ResumeEducation> resumeEducationList = resumeEducationMapper.selectList(new QueryWrapper<ResumeEducation>()
                .eq("user_id", userId)
                .eq("resume_id", resume.getId())
                .orderByDesc("begin_date"));

        //  将工作经验列表信息封装到 对应的 VO 中
        resumeVO.setWorkExpList(resumeWorkExpList);
        resumeVO.setProjectExpList(resumeProjectExpList);
        resumeVO.setEducationList(resumeEducationList);
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

    /**
     * 查询单个用户的工作经验列表
     *
     * @param workExpId
     * @param userId
     * @return
     */
    @Override
    public ResumeWorkExp getWorkExp(String workExpId, String userId) {

        ResumeWorkExp exp = resumeWorkExpMapper.selectOne(new QueryWrapper<ResumeWorkExp>()
                .eq("id", workExpId)
                .eq("user_id", userId));
        return exp;
    }

    /**
     * 删除工作经验详情
     *
     * @param workExpId
     * @param userId
     */
    @Override
    public void deleteWorkExp(String workExpId, String userId) {

        //  从数据库中删除工作经验详情
        resumeWorkExpMapper.delete(new QueryWrapper<ResumeWorkExp>()
                .eq("id", workExpId)
                .eq("user_id", userId));

        //  从 redis 中删除 简历信息的缓存
        redis.del(REDIS_RESUME_INFO + ":" + userId);
    }

    /**
     * 新增 编辑项目经验业务实现
     *
     * @param editProjectExpBO
     */
    @Transactional
    @Override
    public void editProjectExp(EditProjectExpBO editProjectExpBO) {

        ResumeProjectExp resumeProjectExp = new ResumeProjectExp();
        BeanUtils.copyProperties(editProjectExpBO, resumeProjectExp);

        resumeProjectExp.setUpdatedTime(LocalDateTime.now());

        //  判断当前的 项目经验 对象是否存在 id 如果存在则 更新 不存在则新增
        if (StringUtils.isBlank(resumeProjectExp.getId())) {
            //  执行新增流程
            resumeProjectExp.setCreateTime(LocalDateTime.now());
            resumeProjectExpMapper.insert(resumeProjectExp);
        } else {
            //  执行更新流程
            resumeProjectExpMapper.update(resumeProjectExp, new QueryWrapper<ResumeProjectExp>()
                    .eq("id", editProjectExpBO.getId())
                    .eq("user_id", editProjectExpBO.getUserId())
                    .eq("resume_id", editProjectExpBO.getResumeId()));
        }

        //  删除 redis 中存储的简历信息缓存
        redis.del(REDIS_RESUME_INFO + ":" + editProjectExpBO.getUserId());
    }

    /**
     * 查询项目经验详情 业务实现
     *
     * @param projectExpId
     * @param userId
     * @return
     */
    @Override
    public ResumeProjectExp getProjectExp(String projectExpId, String userId) {

        ResumeProjectExp exp = resumeProjectExpMapper.selectOne(new QueryWrapper<ResumeProjectExp>().
                eq("id", projectExpId)
                .eq("user_id", userId));
        return exp;
    }

    /**
     * 删除项目经验
     *
     * @param projectExpId
     * @param userId
     */
    @Override
    public void deleteProjectExp(String projectExpId, String userId) {

        resumeProjectExpMapper.delete(new QueryWrapper<ResumeProjectExp>()
                .eq("id", projectExpId)
                .eq("user_id", userId));

        redis.del(REDIS_RESUME_INFO + ":" + userId);
    }

    /**
     * 新增 或者 修改实现方法
     *
     * @param editEducationBO
     */
    @Transactional
    @Override
    public void editEducation(EditEducationBO editEducationBO) {

        ResumeEducation resumeEducation = new ResumeEducation();
        BeanUtils.copyProperties(editEducationBO, resumeEducation);

        resumeEducation.setUpdatedTime(LocalDateTime.now());

        if (StringUtils.isBlank(resumeEducation.getId())) {
            //  执行新增流程
            resumeEducation.setCreateTime(LocalDateTime.now());
            resumeEducationMapper.insert(resumeEducation);
        } else {
            //  执行更新流程
            resumeEducationMapper.update(resumeEducation, new QueryWrapper<ResumeEducation>()
                    .eq("id", editEducationBO.getId())
                    .eq("user_id", editEducationBO.getUserId())
                    .eq("resume_id", editEducationBO.getResumeId()));
        }

        redis.del(REDIS_RESUME_INFO + ":" + editEducationBO.getUserId());
    }

    /**
     * 查询用户 教育经历实现方法
     *
     * @param eduId
     * @param userId
     * @return
     */
    @Override
    public ResumeEducation getEducation(String eduId, String userId) {

        ResumeEducation resumeEducation = resumeEducationMapper.selectOne(new QueryWrapper<ResumeEducation>()
                .eq("id", eduId)
                .eq("user_id", userId));

        return resumeEducation;
    }

    /**
     * 删除教育经历
     *
     * @param eduId
     * @param userId
     */
    @Override
    public void deleteEducation(String eduId, String userId) {

        resumeEducationMapper.delete(new QueryWrapper<ResumeEducation>()
                .eq("id", eduId)
                .eq("user_id", userId));

        //  删除缓存中存储的内容
        redis.del(REDIS_RESUME_INFO + ":" + userId);
    }

    /**
     * 编辑求职期望实现方法
     *
     * @param editResumeExpectBO
     */
    @Transactional
    @Override
    public void editResumeExpect(EditResumeExpectBO editResumeExpectBO) {

        ResumeExpect resumeExpect = new ResumeExpect();
        BeanUtils.copyProperties(editResumeExpectBO, resumeExpect);

        resumeExpect.setUpdatedTime(LocalDateTime.now());

        if (StringUtils.isBlank(resumeExpect.getId())) {
            //  执行新增流程
            resumeExpectMapper.insert(resumeExpect);
        } else {
            //  执行更新流程
            resumeExpectMapper.update(resumeExpect, new QueryWrapper<ResumeExpect>()
                    .eq("id", editResumeExpectBO.getId())
                    .eq("user_id", editResumeExpectBO.getUserId())
                    .eq("resume_id", editResumeExpectBO.getResumeId()));

            redis.del(REDIS_RESUME_INFO + ":" + editResumeExpectBO.getUserId());
        }
    }

    /**
     * 查询我的期望列表
     *
     * @param resumeId
     * @param userId
     * @return
     */
    @Override
    public List<ResumeExpect> getMyResumeExpect(String resumeId, String userId) {

        //  查询 redis 如果查询到了 就类型转换 如果 redis 中没有相关记录 就从 DB 中查询
        String myResumeExpectListJson = redis.get(REDIS_RESUME_EXPECT + ":" + userId);
        List<ResumeExpect> resumeExpectList = null;

        //  判断当前从 缓存中读取的内容是否从存在
        if (StringUtils.isNotBlank(myResumeExpectListJson)) {
            //   对象类型转换
            resumeExpectList = GsonUtils.stringToListAnother(myResumeExpectListJson, ResumeExpect.class);
        } else {
            //  从 DB 中查询相关信息
            resumeExpectList = resumeExpectMapper.selectList(new QueryWrapper<ResumeExpect>()
                    .eq("id", resumeId)
                    .eq("user_id", userId));
        }
        return resumeExpectList;
    }

    /**
     * 删除求职期望业务实现
     *
     * @param resumeId
     * @param userId
     */
    @Override
    public void deleteMyResumeExpect(String resumeId, String userId) {

        resumeExpectMapper.delete(new QueryWrapper<ResumeExpect>()
                .eq("id", resumeId)
                .eq("user_id", userId));

        //  从 redis 中删除相关内容
        redis.del(REDIS_RESUME_INFO + ":" + userId);
    }

    /**
     * 刷新简历业务实现函数
     * @param userId
     * @param resumeId
     */
    @Transactional
    @Override
    public void refreshResume(String userId, String resumeId) {

        EditResumeBO editResumeBO = new EditResumeBO();
        editResumeBO.setId(resumeId);
        editResumeBO.setUserId(userId);

        editResumeBO.setRefreshTime(LocalDateTime.now());
        this.modifyResume(editResumeBO);
    }
}
