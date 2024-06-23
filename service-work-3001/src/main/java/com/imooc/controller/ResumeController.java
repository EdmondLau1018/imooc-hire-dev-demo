package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/resume")
public class ResumeController {


    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * 初始化用户简历接口
     * @param userId
     * @return
     */
    @PostMapping("/init")
    public GraceJSONResult initResume(@RequestParam("userId") String userId){

//        resumeService.initResume(userId);
        return GraceJSONResult.ok();
    }
}
