package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.imooc.api.feign.CompanyMicroServiceFeign;
import com.imooc.api.feign.UserMicroServiceFeign;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.JobStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.mapper.JobMapper;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.pojo.bo.SearchBO;
import com.imooc.pojo.bo.SearchJobsBO;
import com.imooc.pojo.vo.CompanyInfoVO;
import com.imooc.pojo.vo.SearchJobsVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.JobService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private final UserMicroServiceFeign userMicroServiceFeign;

    private final CompanyMicroServiceFeign companyMicroServiceFeign;

    public JobServiceImpl(JobMapper jobMapper, UserMicroServiceFeign userMicroServiceFeign, CompanyMicroServiceFeign companyMicroServiceFeign) {
        this.jobMapper = jobMapper;
        this.userMicroServiceFeign = userMicroServiceFeign;
        this.companyMicroServiceFeign = companyMicroServiceFeign;
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

        if (StringUtils.isNotBlank(companyId)) {
            jobQueryWrapper.eq("company_id", companyId);
        }

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

    /**
     * 查询工作岗位信息 和 工作岗位发布的 hr 信息 企业信息
     *
     * @param searchJobsBO
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PagedGridResult searchJobs(SearchJobsBO searchJobsBO, Integer page, Integer limit) {

        String jobName = searchJobsBO.getJobName();
        String jobType = searchJobsBO.getJobType();
        String city = searchJobsBO.getCity();
        Integer beginSalary = searchJobsBO.getBeginSalary();
        Integer endSalary = searchJobsBO.getEndSalary();

        PageHelper.startPage(page, limit);

        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", JobStatus.OPEN.type);

        if (StringUtils.isNotBlank(jobName)) {
            queryWrapper.like("job_name", jobName);
        }
        if (StringUtils.isNotBlank(jobType)) {
            queryWrapper.like("job_type", jobType);
        }
        if (StringUtils.isNotBlank(city)) {
            queryWrapper.like("city", city);
        }
        if (beginSalary > 0 && endSalary > 0) {
//            queryWrapper.ge("end_salary", beginSalary);
            queryWrapper.and(
                    qw -> qw.or(
                                    //  岗位薪资 begin <= 求职薪资 begin <= 岗位薪资 end
                                    subQW -> subQW.ge("begin_salary", beginSalary)
                                            .le("end_salary", beginSalary)
                                    //  岗位薪资 begin <= 岗位薪资 end <= 岗位薪资 end
                            ).or(
                                    subWQ -> subWQ.ge("begin_salary", endSalary))
                                                .le("end_salary", endSalary)
                            .or(
                                    //  岗位薪资 begin >= 求职薪资 end 岗位薪资 end >= 求职薪资 end
                                    subQW -> subQW.ge("begin_salary", endSalary)
                                            .ge("end_salary", endSalary))
            );
        }

        List<Job> jobList = jobMapper.selectList(queryWrapper);

        //  如果当前 工作岗位列表中查询的结果为 null 那么就不需要搜索了
        if (jobList == null || jobList.isEmpty() || jobList.size() == 0)
            return setterPagedGrid(jobList, page);

        //  构建 HR id 列表 和企业 id 列表用来作为远程查询对象的参数
        List<String> hrIds = new ArrayList<>();
        ArrayList<String> companyIds = new ArrayList<>();

        //  构建 VO 对象 用来返回给前端
        List<SearchJobsVO> searchJobsVOList = new ArrayList<>();
        for (Job job : jobList) {
            companyIds.add(job.getCompanyId());
            hrIds.add(job.getHrId());

            SearchJobsVO searchJobsVO = new SearchJobsVO();
            BeanUtils.copyProperties(job, searchJobsVO);

            searchJobsVOList.add(searchJobsVO);
        }

        // 查询 hr 用户信息
        SearchBO searchBO = new SearchBO();
        //  查询参数对象 添加 id 列表
        searchBO.setUserIds(hrIds);
        GraceJSONResult graceJSONResult = userMicroServiceFeign.getList(searchBO);
        String userListStr = (String) graceJSONResult.getData();
        //  将获取的对象转换成 hr 用户 VO 列表
        List<UsersVO> hrUsersVoList = GsonUtils.stringToListAnother(userListStr, UsersVO.class);
        //  根据工作岗位结果查询的 hr id 将对应的 hr 信息 设置到对应的工作岗位信息中
        for (SearchJobsVO searchJobsVO : searchJobsVOList) {
            for (UsersVO usersVO : hrUsersVoList) {
                if (searchJobsVO.getHrId().equalsIgnoreCase(usersVO.getId())) {
                    //  确认当前岗位对应的 hr id 对应的 就是 用户VO中的 hr 信息
                    searchJobsVO.setUsersVO(usersVO);
                }
            }
        }

        //  同理 查询 工作岗位对应的企业信息
        GraceJSONResult graceJSONResultCompany = companyMicroServiceFeign.getList(searchBO);
        String companyInfoVOStr = (String) graceJSONResultCompany.getData();
        //  将 jsonString 转换成 公司对象信息列表
        List<CompanyInfoVO> companyInfoVOList = GsonUtils.stringToListAnother(companyInfoVOStr, CompanyInfoVO.class);
        //  根据 搜索工作岗位 对应的 企业 id 将企业信息 封装到返回对象中
        for (SearchJobsVO searchJobsVO : searchJobsVOList) {
            for (CompanyInfoVO companyInfoVO : companyInfoVOList) {
                if (searchJobsVO.getCompanyId().equalsIgnoreCase(companyInfoVO.getCompanyId())) {
                    searchJobsVO.setCompanyInfoVO(companyInfoVO);
                }
            }
        }

        PagedGridResult gridResult = setterPagedGrid(searchJobsVOList, page);
        return gridResult;
    }


}
