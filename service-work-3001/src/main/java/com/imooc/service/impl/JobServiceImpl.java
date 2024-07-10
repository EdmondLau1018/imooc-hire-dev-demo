package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.mapper.JobMapper;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.service.JobService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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


}
