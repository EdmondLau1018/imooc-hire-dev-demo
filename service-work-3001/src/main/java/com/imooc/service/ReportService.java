package com.imooc.service;

import com.imooc.pojo.mo.ReportMO;

public interface ReportService {

    /**
     * 新增举报记录数据
     * @param reportMO
     */
    public void saveReportRecord(ReportMO reportMO);
}
