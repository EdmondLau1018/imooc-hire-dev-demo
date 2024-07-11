package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.JobStatus;
import com.imooc.mapper.JobMapper;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.service.JobService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * HR发布的职位表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class JobServiceImpl extends BaseInfoProperties implements JobService {

    private final JobMapper jobMapper;

    public JobServiceImpl(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    /**
     * 修改工作岗位信息业务实现
     *
     * @param editJobBO
     */
    @Override
    public void modifyJobDetail(EditJobBO editJobBO) {

        Job job = new Job();
        BeanUtils.copyProperties(editJobBO, job);

        job.setUpdatedTime(LocalDateTime.now());

        //  根据 id 判断需要进行的是新增流程还是修改流程
        if (StringUtils.isBlank(editJobBO.getId())) {
            //  进入新增业务逻辑
            job.setCreateTime(LocalDateTime.now());
            jobMapper.insert(job);
        } else {
            //  进入修改业务逻辑
            jobMapper.update(job, new QueryWrapper<Job>().eq("id", editJobBO.getId())
                    .eq("hr_id", editJobBO.getHrId())
                    .eq("company_id", editJobBO.getCompanyId()));
        }

        //  删除redis 缓存中的内容
        redis.del(REDIS_JOB_DETAIL + ":" + editJobBO.getCompanyId() + ":"
                + editJobBO.getHrId() + ":"
                + editJobBO.getId());
    }

    /**
     * 分页查询 hr 发布的工作机会列表
     *
     * @param hrId
     * @param companyId
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PagedGridResult queryHrJobList(String hrId, String companyId, Integer page, Integer pageSize, Integer status) {

        PageHelper.startPage(page, pageSize);
        QueryWrapper<Job> jobQueryWrapper = new QueryWrapper<>();
        //  如果有 id 查询当前 hr 发布的职位
        if (StringUtils.isNotBlank(hrId)) {
            jobQueryWrapper.eq("hr_id", hrId);
        }
        jobQueryWrapper.eq("company_id", companyId);

        //  判断当前的岗位 是否为 null
        if (status != null) {
            if (status == JobStatus.OPEN.type ||
                    status == JobStatus.CLOSE.type ||
                    status == JobStatus.DELETE.type) {
                jobQueryWrapper.eq("status", status);
            }
        }

        jobQueryWrapper.orderByDesc("update_time");

        //  从 DB 查询对应的 岗位列表
        List<Job> jobList = jobMapper.selectList(jobQueryWrapper);
        return setterPagedGrid(jobList, page);
    }

    /**
     * 根据 id 查询尊位详情
     *
     * @param companyId
     * @param hrId
     * @param jobId
     * @return
     */
    @Override
    public Job queryHrJobDetail(String companyId, String hrId, String jobId) {

        //  定义不同状态 在数据库中存储的值
        Integer[] status = {1, 2, 3};

        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", jobId);

        if (StringUtils.isNotBlank(companyId)) {
            queryWrapper.eq("company_id", companyId);
        }

        if (StringUtils.isNotBlank(hrId)) {
            queryWrapper.eq("hr_id", hrId);
        }

        queryWrapper.in("status", status);

        Job job = jobMapper.selectOne(queryWrapper);

        //  将查询到的结果设置在缓存中
        redis.set(REDIS_JOB_DETAIL + ":" + companyId + ":" + hrId + ":" + jobId,
                GsonUtils.object2String(job));
        return job;
    }

    /**
     * 修改工作岗位状态接口
     *
     * @param companyId
     * @param hrId
     * @param jobStatus
     */
    @Override
    public void modifyJobStatus(String jobId, String companyId, String hrId, JobStatus jobStatus) {

        Job job = new Job();

        job.setStatus(jobStatus.type);
        job.setUpdatedTime(LocalDateTime.now());

        jobMapper.update(job, new QueryWrapper<Job>()
                .eq("job_id", jobId)
                .eq("company_id", companyId)
                .eq("hr_id", hrId));

        //  删除缓存中的内容
        redis.del(REDIS_JOB_DETAIL + ":" + companyId + ":" + hrId + ":" + jobId);
    }


}
