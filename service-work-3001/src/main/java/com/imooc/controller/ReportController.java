package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.DealStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.bo.SearchReportJobBO;
import com.imooc.pojo.mo.ReportMO;
import com.imooc.service.ReportService;
import com.imooc.utils.LocalDateUtils;
import com.imooc.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    /**
     * 分页查询 职位举报列表
     *
     * @param searchReportJobBO
     * @return
     */
    @PostMapping("/pagedReportRecordList")
    public GraceJSONResult pagedReportRecordList(@RequestBody SearchReportJobBO searchReportJobBO, Integer page, Integer pageSize) {

        //  mongoDB 从 0 开始分页的
        if (page == null) page = COMMON_START_PAGE_ZERO;
        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        LocalDate beginDate = searchReportJobBO.getBeginDate();
        LocalDate endDate = searchReportJobBO.getEndDate();

        //  日期格式转换 加上具体的时间 转换成 日期时间格式
        if (beginDate != null) {
            String beginDateTimeStr = LocalDateUtils.format(beginDate,
                    LocalDateUtils.DATETIME_PATTERN) + " 00:00:00";
            //  将 拼接好的字符串转换为 日期
            LocalDateTime beginDateTime = LocalDateUtils.parseLocalDateTime(beginDateTimeStr,
                    LocalDateUtils.DATETIME_PATTERN);

            searchReportJobBO.setBeginDateTime(beginDateTime);
        }

        if (endDate != null) {
            String endDateTimeStr = LocalDateUtils.format(endDate,
                    LocalDateUtils.DATE_PATTERN) + " 23:59:59";
            //  将拼接好的字符串转换成 日期时间类型的对象
            LocalDateTime endDateTime = LocalDateUtils.parseLocalDateTime(endDateTimeStr,
                    LocalDateUtils.DATETIME_PATTERN);

            searchReportJobBO.setEndDateTime(endDateTime);
        }

        PagedGridResult gridResult = reportService.pagedReportRecordList(searchReportJobBO, page, pageSize);

        return GraceJSONResult.ok(gridResult);
    }


    /**
     * 删除职位
     *
     * @param reportId
     * @return
     */
    @PostMapping("/deal/delete")
    public GraceJSONResult dealDelete(String reportId) {

        reportService.updateReportRecordStatus(reportId, DealStatus.DONE);
        return GraceJSONResult.ok();
    }

    @PostMapping("/deal/ignore")
    public GraceJSONResult dealIgnore(String reportId) {

        reportService.updateReportRecordStatus(reportId, DealStatus.IGNORE);
        return GraceJSONResult.ok();
    }

}
