package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
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

        //  首先判断举报信息是否存在
        boolean exist = reportService.isReportExist(reportMO.getReportUserId(),
                reportMO.getJobId());

        if (exist) {
            //  如果当前举报信息存在 返回 举报信息存在 的错误
            GraceJSONResult.errorCustom(ResponseStatusEnum.REPORT_RECORD_EXIST_ERROR);
        }

        reportService.saveReportRecord(reportMO);
        return GraceJSONResult.ok();
    }

}
