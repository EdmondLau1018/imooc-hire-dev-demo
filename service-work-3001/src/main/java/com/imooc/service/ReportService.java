package com.imooc.service;

import com.imooc.enums.DealStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.SearchReportJobBO;
import com.imooc.pojo.mo.ReportMO;
import com.imooc.utils.PagedGridResult;

public interface ReportService {

    /**
     * 新增举报记录数据
     *
     * @param reportMO
     */
    public void saveReportRecord(ReportMO reportMO);

    /**
     * 根据用户 id 和 岗位 id 查询举报信息是否存在
     *
     * @param reportUserId
     * @param jobId
     * @return
     */
    public boolean isReportExist(String reportUserId, String jobId);

    /**
     * mongoDB 查询举报职位列表
     * @param searchReportJobBO
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult pagedReportRecordList(SearchReportJobBO searchReportJobBO,
                                                 Integer page,
                                                 Integer pageSize);

    /**
     * 更新 mongoDB 和 数据库中存储的岗位状态
     * @param reportId
     * @param status
     */
    public void updateReportRecordStatus(String reportId, DealStatus status);
}
