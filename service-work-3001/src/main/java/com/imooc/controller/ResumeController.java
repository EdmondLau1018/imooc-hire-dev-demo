package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.ActiveTime;
import com.imooc.enums.EduEnum;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.ResumeEducation;
import com.imooc.pojo.ResumeProjectExp;
import com.imooc.pojo.ResumeWorkExp;
import com.imooc.pojo.bo.*;
import com.imooc.pojo.vo.ResumeVO;
import com.imooc.service.ResumeSearchService;
import com.imooc.service.ResumeService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.LocalDateUtils;
import com.imooc.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/resume")
public class ResumeController extends BaseInfoProperties {


    private final ResumeService resumeService;

    private final ResumeSearchService resumeSearchService;

    public ResumeController(ResumeService resumeService, ResumeSearchService resumeSearchService) {
        this.resumeService = resumeService;
        this.resumeSearchService = resumeSearchService;
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
     *
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

    /**
     * 修改 用户工作经验接口
     *
     * @param editWorkExpBO
     * @return
     */
    @PostMapping("/editWorkExp")
    public GraceJSONResult editWorkExp(@RequestBody @Valid EditWorkExpBO editWorkExpBO) {

        resumeService.editWorkExp(editWorkExpBO);
        return GraceJSONResult.ok();
    }

    /**
     * 获取单个用户工作经验详情接口
     *
     * @param workExpId
     * @param userId
     * @return
     */
    @PostMapping("/getWorkExp")
    public GraceJSONResult getWorkExp(String workExpId, String userId) {

        ResumeWorkExp workExp = resumeService.getWorkExp(workExpId, userId);
        return GraceJSONResult.ok(workExp);
    }

    /**
     * 删除工作经验详情内容
     *
     * @param workWxpId
     * @param userId
     * @return
     */
    @PostMapping("deleteWorkExp")
    public GraceJSONResult deleteWorkExp(String workWxpId, String userId) {

        resumeService.deleteWorkExp(workWxpId, userId);
        return GraceJSONResult.ok();
    }

    /**
     * 新增或者 更新项目经验 接口
     *
     * @param editProjectExpBO
     * @return
     */
    @PostMapping("/editProjectExp")
    public GraceJSONResult editProjectExp(@RequestBody @Valid EditProjectExpBO editProjectExpBO) {

        resumeService.editProjectExp(editProjectExpBO);
        return GraceJSONResult.ok();
    }


    /**
     * 查询 工作项目经验 列表
     *
     * @param projectExpId
     * @param userId
     * @return
     */
    @PostMapping("/getProjectExp")
    public GraceJSONResult getProjectExp(String projectExpId, String userId) {

        ResumeProjectExp exp = resumeService.getProjectExp(projectExpId, userId);

        return GraceJSONResult.ok(exp);
    }

    /**
     * 删除用户项目经验信息接口
     *
     * @param projectId
     * @param userId
     * @return
     */
    @PostMapping("/deleteProjectExp")
    public GraceJSONResult deleteProjectExp(String projectId, String userId) {

        resumeService.deleteProjectExp(projectId, userId);
        return GraceJSONResult.ok();
    }

    /**
     * 编辑教育经历
     *
     * @param editEducationBO
     * @return
     */
    @PostMapping("/editEducation")
    public GraceJSONResult editEducation(@RequestBody @Valid EditEducationBO editEducationBO) {

        resumeService.editEducation(editEducationBO);
        return GraceJSONResult.ok();
    }

    /**
     * 查询教育经历详情
     *
     * @param eduId
     * @param userId
     * @return
     */
    @PostMapping("/getEducation")
    public GraceJSONResult getEducation(String eduId, String userId) {

        ResumeEducation resumeEducation = resumeService.getEducation(eduId, userId);
        return GraceJSONResult.ok(resumeEducation);
    }

    /**
     * 删除教育经历详情
     *
     * @param eduId
     * @param userId
     * @return
     */
    @PostMapping("/deleteEducation")
    public GraceJSONResult deleteEducation(String eduId, String userId) {

        resumeService.deleteEducation(eduId, userId);
        return GraceJSONResult.ok();
    }

    /**
     * 编辑求职期望
     *
     * @param resumeExpectBO
     * @return
     */
    @PostMapping("/editJobEExpect")
    public GraceJSONResult editJobExpect(@RequestBody @Valid EditResumeExpectBO resumeExpectBO) {

        resumeService.editResumeExpect(resumeExpectBO);

        return GraceJSONResult.ok();
    }

    /**
     * @param resumeId
     * @param userId
     * @return
     */
    @PostMapping("/getMyResumeExpectList")
    public GraceJSONResult getMyResumeExpectList(String resumeId, String userId) {

        resumeService.getMyResumeExpect(resumeId, userId);

        return GraceJSONResult.ok();
    }

    /**
     * 求职期望删除
     *
     * @param resumeExpectId
     * @param userId
     * @return
     */
    @PostMapping("/deleteMyResumeExpect")
    public GraceJSONResult deleteMyResumeExpect(String resumeExpectId, String userId) {

        resumeService.deleteMyResumeExpect(resumeExpectId, userId);

        return GraceJSONResult.ok();
    }

    /**
     * 刷新简历接口
     *
     * @param userId
     * @param resumeId
     * @return
     */
    @PostMapping("/refresh")
    public GraceJSONResult refreshResume(String userId, String resumeId) {

        //  假设从 feign 中获取系统参数中配置的 最大刷新简历次数
        int maxResumeRefreshCounts = 3;

        //  从 redis 中 获取当天 已刷新的次数 ，如果大于等于该系统参数配置的次数则返回错误 如果小于这个次数则可以刷新
        String localDateStr = LocalDateUtils.getLocalDateStr();
        int userAlreadyRefreshedCounts = 0;
        //  获取当前用户 今日 刷新简历的次数
        String userAlreadyRefreshedCountsStr = redis.get(USER_ALREADY_REFRESHED_COUNTS + ":" + localDateStr + ":" + userId);
        //  为空 则表示 当前用户今天没有刷新过简历 设置 0 这个缓存只有在当天才会被查询，设置 24 小时的缓存时间
        if (StringUtils.isBlank(userAlreadyRefreshedCountsStr)) {

            redis.set(USER_ALREADY_REFRESHED_COUNTS + ":" + localDateStr + ":" + userId,
                    userAlreadyRefreshedCounts + "",
                    24 * 60 * 60);
        } else {

            //  不为空 ，直接将缓存中存储的 数量转换成 int进行判断
            userAlreadyRefreshedCounts = Integer.valueOf(userAlreadyRefreshedCountsStr);
        }

        //  如果当前 简历刷新次数小于系统参数 则 将信息保存到数据库中并刷新到 redis 中
        if (userAlreadyRefreshedCounts <= maxResumeRefreshCounts) {
            resumeService.refreshResume(userId, resumeId);
            //  redis 信息同步
            redis.increment(USER_ALREADY_REFRESHED_COUNTS + ":" + localDateStr + ":" + userId, 1);
        } else {
            //  刷新次数超过限制 抛出超限提示 返回错误信息
            return GraceJSONResult.errorCustom(ResponseStatusEnum.RESUME_MAX_LIMIT_ERROR);
        }
        //  ES 重新加载简历信息
        resumeSearchService.transformAndFlush(userId);

        return GraceJSONResult.ok();
    }

    /**
     * 分页查询简历信息
     *
     * @param searchResumesBO
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("/searchResumes")
    public GraceJSONResult searchResumes(@RequestBody SearchResumesBO searchResumesBO,
                                         Integer page, Integer limit) {

        //  使用枚举索引 查询 活跃时间 对应的 索引值
        String activeTime = searchResumesBO.getActiveTime();
        Integer activeTimes = ActiveTime.getActiveTimes(activeTime);
        searchResumesBO.setActiveTimes(activeTimes);

        //  使用枚举索引 查询对应的 教育经历索引值
        String edu = searchResumesBO.getEdu();
        Integer eduIndex = EduEnum.getEduIndex(edu);
        List eduList = EduEnum.getEduList(eduIndex);
        searchResumesBO.setEduList(eduList);

//        PagedGridResult gridResult = resumeService.searchResumes(searchResumesBO, page, limit);
        PagedGridResult gridResult = resumeService.searchResumesByES(searchResumesBO, page, limit);

        return GraceJSONResult.ok(gridResult);
    }
}
