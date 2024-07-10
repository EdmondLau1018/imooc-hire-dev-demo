package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.service.JobService;
import lombok.extern.slf4j.Slf4j;
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
}
