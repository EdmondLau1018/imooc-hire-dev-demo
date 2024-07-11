package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.JobStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.service.JobService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobController extends BaseInfoProperties {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * HR 新增/修改职位校验
     *
     * @param editJobBO
     * @return
     */
    @PostMapping("/modify")
    public GraceJSONResult modify(@RequestBody @Valid EditJobBO editJobBO) {
        jobService.modifyJobDetail(editJobBO);
        return GraceJSONResult.ok();
    }

    /**
     * hr 工作列表分页查询
     *
     * @param hrId
     * @param companyId
     * @param page
     * @param limit
     * @param status
     * @return
     */
    @PostMapping("/hr/jobList")
    public GraceJSONResult hrJobList(String hrId, String companyId, Integer page, Integer limit, Integer status) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        if (StringUtils.isBlank(companyId)) {
            return GraceJSONResult.error();
        }

        PagedGridResult gridResult = jobService.queryHrJobList(hrId, companyId, page, limit, status);

        return GraceJSONResult.ok(gridResult);
    }

    /**
     * 查询工作历史记录
     *
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("/jobList")
    public GraceJSONResult getJobList(Integer page, Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 1;

        PagedGridResult gridResult = jobService.queryHrJobList(null, null, page, limit, null);

        return GraceJSONResult.ok(gridResult);
    }

    /**
     * 查询职位详情（历史信息 - - 职位详情）
     *
     * @param jobId
     * @return
     */
    @PostMapping("/admin/jobDetail")
    public GraceJSONResult adminJobDetail(String jobId) {

        if (StringUtils.isBlank(jobId)) {
            return GraceJSONResult.error();
        }

        Job job = jobService.queryHrJobDetail(null, null, jobId);
        return GraceJSONResult.ok(job);
    }

    /**
     * @param jobId
     * @param hrId
     * @param companyId
     * @return
     */
    @PostMapping("/hr/jobDetail")
    public GraceJSONResult hrJobDetail(String jobId, String hrId, String companyId) {

        //  参数校验
        if (StringUtils.isBlank(jobId) ||
                StringUtils.isBlank(hrId) ||
                StringUtils.isBlank(companyId))
            return GraceJSONResult.error();

        //  从 redis 中查询对应的信息
        String jobDetailStr = redis.get(REDIS_JOB_DETAIL + ":" + companyId + ":" + hrId + ":" + jobId);

        Job job = null;
        if (StringUtils.isBlank(jobDetailStr)) {
            //  从 缓存中未查询出数据 从 DB中进行查询
            job = jobService.queryHrJobDetail(jobId, hrId, companyId);
        } else {
            job = GsonUtils.stringToBean(jobDetailStr, Job.class);
        }

        return GraceJSONResult.ok(job);
    }

    /**
     * 开启和关闭工作 岗位 状态接口
     *
     * @param jobId
     * @param hrId
     * @param companyId
     * @return
     */
    @PostMapping("/close")
    public GraceJSONResult closeJob(String jobId, String hrId, String companyId) {

        jobService.modifyJobStatus(jobId, companyId, hrId, JobStatus.CLOSE);
        return GraceJSONResult.ok();
    }

    @PostMapping("/open")
    public GraceJSONResult openJob(String jobId, String hrId, String companyId) {

        jobService.modifyJobStatus(jobId, companyId, hrId, JobStatus.OPEN);
        return GraceJSONResult.ok();
    }

}
