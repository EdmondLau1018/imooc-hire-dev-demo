package com.imooc.service.impl;

import com.imooc.enums.DealStatus;
import com.imooc.mapper.ReportJobRepository;
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
     * @param reportMO
     */
    @Override
    public void saveReportRecord(ReportMO reportMO) {

        reportMO.setDealStatus(DealStatus.WAITING.type);
        reportMO.setCreatedTime(LocalDateTime.now());
        reportMO.setUpdatedTime(LocalDateTime.now());

        reportJobRepository.save(reportMO);
    }
}
