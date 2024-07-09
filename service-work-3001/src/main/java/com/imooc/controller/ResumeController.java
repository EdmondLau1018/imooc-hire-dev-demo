package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.EditResumeBO;
import com.imooc.pojo.bo.EditWorkExpBO;
import com.imooc.pojo.vo.ResumeVO;
import com.imooc.service.ResumeService;
import com.imooc.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/resume")
public class ResumeController extends BaseInfoProperties {


    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * 初始化用户简历接口
     *
     * @param userId
     * @return
     */
    @PostMapping("/init")
    public GraceJSONResult initResume(@RequestParam("userId") String userId) {

//        resumeService.initResume(userId);
        return GraceJSONResult.ok();
    }

    /**
     * 修改用户求职简历的接口
     *
     * @param editResumeBO
     * @return
     */
    @PostMapping("/modify")
    public GraceJSONResult modifyResume(@RequestBody @Valid EditResumeBO editResumeBO) {

        resumeService.modifyResume(editResumeBO);
        return GraceJSONResult.ok();
    }

    /**
     * 根据当前  userId 获取用户简历信息
     * @param userId
     * @return
     */
    @PostMapping("/getMyResume")
    public GraceJSONResult getMyResume(String userId) {

        if (StringUtils.isBlank(userId))
            return GraceJSONResult.error();

        //  从 redis 中查询当前用户的简历信息如果查询不到 从 DB 中查询当前用户的简历信息
        String resumeJson = redis.get(REDIS_RESUME_INFO + ":" + userId);
        ResumeVO resumeVO = null;
        if (StringUtils.isNotBlank(resumeJson)) {
            resumeVO = GsonUtils.stringToBean(resumeJson, resumeVO.getClass());
        } else {
            //  在 redis 中没有查到 查询数据库
            resumeVO = resumeService.getResumeInfo(userId);
            // 将查询结果设置到 redis 中
            redis.set(REDIS_RESUME_INFO + ":" + userId, GsonUtils.object2String(resumeVO));
        }
        return GraceJSONResult.ok(resumeVO);
    }

    @PostMapping("/editWorkExp")
    public GraceJSONResult editWorkExp(@RequestBody @Valid EditWorkExpBO editWorkExpBO) {

        resumeService.editWorkExp(editWorkExpBO);
        return GraceJSONResult.ok();
    }
}
