package com.imooc.service.impl;

import com.imooc.mapper.ResumeMapper;
import com.imooc.pojo.Resume;
import com.imooc.service.ResumeService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

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
    //  @Transactional      //  新增数据 加上本地事务注解
    @GlobalTransactional    //  将本地事务切换为分布式事务
    @Override
    public void initResume(String userId) {

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());

        resumeMapper.insert(resume);
    }
}
