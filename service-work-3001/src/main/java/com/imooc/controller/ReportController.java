package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.mo.ReportMO;
import com.imooc.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController extends BaseInfoProperties {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 保存举报信息接口
     *
     * @param reportMO
     * @return
     */
    @PostMapping("/create")
    public GraceJSONResult create(@RequestBody @Valid ReportMO reportMO) {

        reportService.saveReportRecord(reportMO);
        return GraceJSONResult.ok();
    }

}
