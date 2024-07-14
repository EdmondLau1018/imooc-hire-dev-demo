package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.DealStatus;
import com.imooc.enums.JobStatus;
import com.imooc.mapper.JobMapper;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.SearchReportJobBO;
import com.imooc.pojo.mo.ReportMO;
import com.imooc.repository.ReportJobRepository;
import com.imooc.service.ReportService;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ReportServiceImpl extends BaseInfoProperties implements ReportService {

    private final ReportJobRepository reportJobRepository;

    private final MongoTemplate mongoTemplate;

    private final JobMapper jobMapper;

    public ReportServiceImpl(ReportJobRepository reportJobRepository, MongoTemplate mongoTemplate, JobMapper jobMapper) {
        this.reportJobRepository = reportJobRepository;
        this.mongoTemplate = mongoTemplate;
        this.jobMapper = jobMapper;
    }

    /**
     * 保存 举报信息业务方法
     *
     * @param reportMO
     */
    @Override
    public void saveReportRecord(ReportMO reportMO) {

        reportMO.setDealStatus(DealStatus.WAITING.type);
        reportMO.setCreatedTime(LocalDateTime.now());
        reportMO.setUpdatedTime(LocalDateTime.now());

        reportJobRepository.save(reportMO);
    }

    /**
     * 根据举报信息和岗位信息 查询举报信息是否存在
     *
     * @param reportUserId
     * @param jobId
     * @return
     */
    @Override
    public boolean isReportExist(String reportUserId, String jobId) {

        ReportMO record = reportJobRepository.findByReportUserIdAndJobId(reportUserId, jobId);
        //  三元表达式 判断当前 record 是否存在
        return record == null ? false : true;
    }

    /**
     * mongoDB 分页查询举报职位列表
     *
     * @param searchReportJobBO
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult pagedReportRecordList(SearchReportJobBO searchReportJobBO, Integer page, Integer pageSize) {

        String jobName = searchReportJobBO.getJobName();
        String companyName = searchReportJobBO.getCompanyName();
        String reportUserName = searchReportJobBO.getReportUserName();
        Integer dealStatus = searchReportJobBO.getDealStatus();
        LocalDateTime beginDateTime = searchReportJobBO.getBeginDateTime();
        LocalDateTime endDateTime = searchReportJobBO.getEndDateTime();

        //  创建查询对象
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();

        //  创建 条件对象
        Criteria criteria = new Criteria();
        //  设置查询条件
        if (StringUtils.isNotBlank(jobName)) {
            addLikeByValue(query, "job_name", jobName);
        }
        if (StringUtils.isNotBlank(companyName)) {
            addLikeByValue(query, "company_name", companyName);
        }
        if (StringUtils.isNotBlank(reportUserName)) {
            addLikeByValue(query, "report_user_name", reportUserName);
        }
        if (dealStatus != null) {
            query.addCriteria(Criteria.where("deal_status").is(dealStatus));
        }

        if (beginDateTime != null && endDateTime == null) {
            query.addCriteria(Criteria.where("created_time").gte(beginDateTime));
        } else if (beginDateTime == null && endDateTime != null) {
            query.addCriteria(Criteria.where("created_time").lte(endDateTime));
        } else if (beginDateTime != null && endDateTime != null) {
            query.addCriteria(Criteria.where("created_time").gte(beginDateTime).lte(endDateTime));
        }

        //  在分页之前查询总数
        long count = mongoTemplate.count(query, ReportMO.class);

        // 设置分页
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "created_time");
        //  分页条件设置到查询条件里
        query.with(pageable);

        List<ReportMO> list = mongoTemplate.find(query, ReportMO.class);

        // 封装分页
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        gridResult.setRecords(count);
        return gridResult;
    }

    /**
     * 更新 mongoDB 和 DB 中的数据内容
     *
     * @param reportId
     * @param status
     */
    @Transactional
    @Override
    public void updateReportRecordStatus(String reportId, DealStatus status) {

        //  添加查询条件 在 mongodb 中查询需要的数据 (使用 update 匹配)
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(reportId));
        //  更新内容（更新的字段名称和值）
        Update update = new Update();
        update.set("deal_status", status.type);
        update.set("updated_time", LocalDateTime.now());

        //  如果是需要处理的 修改职位为 【违规删除】即可
        if (status == DealStatus.DONE) {
            //  在 mongoDB 中 根据举报信息查询对应的岗位信息
            ReportMO temp = reportJobRepository.findById(reportId).get();

            //  在 DB 中更新对应的 岗位信息状态
            String jobId = temp.getJobId();
            Job pendingJob = new Job();
            pendingJob.setId(jobId);
            pendingJob.setStatus(JobStatus.DELETE.type);
            pendingJob.setViolateReason(temp.getReportReason());
            pendingJob.setUpdatedTime(LocalDateTime.now());
            jobMapper.updateById(pendingJob);

        }
        mongoTemplate.updateFirst(query, update, ReportMO.class);
    }

    /**
     * 拼接查询的正则表达式 和查询参数
     *
     * @param query
     * @param key
     * @param value
     * @return
     */
    private org.springframework.data.mongodb.core.query.Query addLikeByValue(Query query, String key, String value) {

        Pattern pattern = Pattern.compile("^.*" + value + ".*$");
        query.addCriteria(Criteria.where(key).regex(pattern));

        return query;
    }
}
