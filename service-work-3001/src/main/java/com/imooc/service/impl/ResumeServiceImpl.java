package com.imooc.service.impl;

import com.imooc.mapper.ResumeMapper;
import com.imooc.pojo.Resume;
import com.imooc.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;

    public ResumeServiceImpl(ResumeMapper resumeMapper) {
        this.resumeMapper = resumeMapper;
    }

    /**
     * 初始化用户简历
     *
     * @param userId 用户 id
     */
    @Transactional      //  新增数据 加上本地事务注解
    @Override
    public void initResume(String userId) {

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());

        resumeMapper.insert(resume);
    }
}
