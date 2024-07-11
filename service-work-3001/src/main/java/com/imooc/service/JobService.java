package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.enums.JobStatus;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.pojo.bo.SearchJobsBO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

/**
 * <p>
 * HR发布的职位表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface JobService {

    /**
     * 新增 更新 工作机会
     *
     * @param editJobBO
     */
    public void modifyJobDetail(EditJobBO editJobBO);

    /**
     * hr 查询发布的工作机会 列表
     *
     * @param hrId
     * @param companyId
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public PagedGridResult queryHrJobList(String hrId, String companyId, Integer page, Integer pageSize, Integer status);

    /**
     * 查询工作机会 详情业务接口
     *
     * @param companyId
     * @param hrId
     * @param jobId
     * @return
     */
    public Job queryHrJobDetail(String companyId, String hrId, String jobId);

    /**
     * 修改工作岗位状态的业务接口，开启和关闭并工作岗位用的都是这个
     *
     * @param companyId
     * @param hrId
     * @param jobStatus
     */
    public void modifyJobStatus(String jobId, String companyId, String hrId, JobStatus jobStatus);

    /**
     *
     * @param searchJobsBO
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult searchJobs(SearchJobsBO searchJobsBO, Integer page, Integer limit);
}
