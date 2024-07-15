package com.imooc.service.impl;

import com.imooc.api.feign.UserMicroServiceFeign;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.ResumeEducation;
import com.imooc.pojo.ResumeExpect;
import com.imooc.pojo.ResumeWorkExp;
import com.imooc.pojo.eo.SearchResumesEO;
import com.imooc.pojo.vo.ResumeVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.ResumeSearchService;
import com.imooc.service.ResumeService;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.LocalDateUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeSearchServiceImpl implements ResumeSearchService {

    private final ElasticsearchRestTemplate esTemplate;

    private final ResumeService resumeService;

    private final UserMicroServiceFeign userMicroServiceFeign;

    public ResumeSearchServiceImpl(ElasticsearchRestTemplate esTemplate, ResumeService resumeService, UserMicroServiceFeign userMicroServiceFeign) {
        this.esTemplate = esTemplate;
        this.resumeService = resumeService;
        this.userMicroServiceFeign = userMicroServiceFeign;
    }

    @Override
    public void transformAndFlush(String userId) {

        SearchResumesEO resumesEO = new SearchResumesEO();
        resumesEO.setUserId(userId);

        //  查询用户简历相关信息
        ResumeVO resumeInfo = resumeService.getResumeInfo(userId);
        //  远程调用查询用户信息
        UsersVO usersVO = getUserInfoVo(userId);

        //  用户相关信息放在 ES 对象中
        resumesEO.setResumeId(resumeInfo.getId());
        resumesEO.setNickname(usersVO.getNickname());
        resumesEO.setSex(usersVO.getSex());
        resumesEO.setFace(usersVO.getFace());
        resumesEO.setBirthday(LocalDateUtils.format(usersVO.getBirthday(),
                LocalDateUtils.DATE_PATTERN));

        //  计算年龄
        Long years = LocalDateUtils.getChronoUnitBetween(usersVO.getBirthday(),
                LocalDate.now(),
                ChronoUnit.YEARS,
                true);

        resumesEO.setAge(years.intValue() + 1);

        //  简历相关信息存储到 ES 对象中
        resumesEO.setSkills(resumeInfo.getSkills());
        resumesEO.setAdvantage(resumeInfo.getAdvantage());
        resumesEO.setAdvantageHtml(resumeInfo.getAdvantageHtml());
        resumesEO.setCredentials(resumeInfo.getCredentials());
        resumesEO.setJobStatus(resumeInfo.getStatus());
        resumesEO.setRefreshTime(LocalDateUtils.format(resumeInfo.getRefreshTime(),
                LocalDateUtils.DATETIME_PATTERN));

        //  计算工作年限
        Long workYears = LocalDateUtils.getChronoUnitBetween(usersVO.getStartWorkDate(),
                LocalDate.now(),
                ChronoUnit.YEARS,
                true);

        resumesEO.setWorkYears(workYears.intValue());

        //  将 最近就职的企业信息存储在 ES 对象中
        List<ResumeWorkExp> workExpList = resumeInfo.getWorkExpList();
        //  检索 最后的工作履历并且返回
        Optional<ResumeWorkExp> lastWorkOpt = workExpList
                .stream()
                .max(Comparator.comparing(ResumeWorkExp::getBeginDate));

        ResumeWorkExp lastWorkExp = lastWorkOpt.get();

        resumesEO.setCompanyName(lastWorkExp.getCompanyName());
        resumesEO.setPosition(lastWorkExp.getPosition());
        resumesEO.setIndustry(lastWorkExp.getIndustry());

        //  将最近一次的教育经历存储在 ES对象中
        List<ResumeEducation> educationList = resumeInfo.getEducationList();
        Optional<ResumeEducation> lastEduOpt = educationList.stream()
                .max(Comparator.comparing(ResumeEducation::getBeginDate));
        ResumeEducation lastEducationExp = lastEduOpt.get();

        resumesEO.setSchool(lastEducationExp.getSchool());
        resumesEO.setEducation(lastEducationExp.getEducation());
        resumesEO.setMajor(lastEducationExp.getMajor());

        //  获得求职期望列表，每个求职期望对应一份简历
        List<ResumeExpect> resumeExpectList = resumeService.getMyResumeExpect(resumeInfo.getId(), userId);
        for (ResumeExpect resumeExpect : resumeExpectList) {
            resumesEO.setResumeId(resumeExpect.getResumeId());
            resumesEO.setJobType(resumeExpect.getJobName());
            resumesEO.setCity(resumeExpect.getCity());
            resumesEO.setBeginSalary(resumeExpect.getBeginSalary());
            resumesEO.setEndSalary(resumesEO.getEndSalary());

            IndexQuery indexQuery = new IndexQueryBuilder().withObject(resumesEO).build();

            //  向 ES 中新增 求职期望数据
            esTemplate.index(indexQuery, IndexCoordinates.of("resume_result"));
        }

    }


    /**
     * 远程调用 用户微服务 查询用户信息
     *
     * @param userId
     * @return
     */
    private UsersVO getUserInfoVo(String userId) {

        GraceJSONResult graceJSONResult = userMicroServiceFeign.get(userId);
        Object data = graceJSONResult.getData();
        String jsonString = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(jsonString, UsersVO.class);

        return usersVO;
    }
}
