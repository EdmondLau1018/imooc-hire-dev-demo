package com.imooc.service.impl;

import com.imooc.enums.DealStatus;
import com.imooc.repository.ReportJobRepository;
import com.imooc.pojo.mo.ReportMO;
import com.imooc.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportJobRepository reportJobRepository;

    public ReportServiceImpl(ReportJobRepository reportJobRepository) {
        this.reportJobRepository = reportJobRepository;
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
}
