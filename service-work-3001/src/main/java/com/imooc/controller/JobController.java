package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.service.JobService;
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

        if (StringUtils.isBlank(companyId)) {
            return GraceJSONResult.error();
        }

        PagedGridResult gridResult = jobService.queryHrJobList(hrId, companyId, page, limit, status);

        return GraceJSONResult.ok(gridResult);
    }

}
