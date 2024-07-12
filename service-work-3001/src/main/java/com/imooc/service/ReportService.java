package com.imooc.service;

import com.imooc.pojo.mo.ReportMO;

public interface ReportService {

    /**
     * 新增举报记录数据
     *
     * @param reportMO
     */
    public void saveReportRecord(ReportMO reportMO);

    /**
     * 根据用户 id 和 岗位 id 查询举报信息是否存在
     * @param reportUserId
     * @param jobId
     * @return
     */
    public boolean isReportExist(String reportUserId, String jobId);
}
