package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.SysParams;
import com.imooc.pojo.vo.SysParamsVO;
import com.imooc.service.SysParamsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sys")
public class SysParamsController {

    private final SysParamsService sysParamsService;

    public SysParamsController(SysParamsService sysParamsService) {
        this.sysParamsService = sysParamsService;
    }

    /**
     * 更新系统参数 简历刷新次数
     *
     * @param maxCounts
     * @param version
     * @return
     */
    @PostMapping("/modifyMaxResumeRefreshCounts")
    public GraceJSONResult modifyMaxResumeRefreshCounts(Integer maxCounts, Integer version) {

        if (maxCounts == null || maxCounts == 0)
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_PARAMS_SETTINGS_ERROR);

        sysParamsService.updateMaxResumeCounts(maxCounts, version);

        return GraceJSONResult.ok(0);
    }

    /**
     * 获得系统参数
     *
     * @return
     */
    @PostMapping("/params")
    public GraceJSONResult params() {

        SysParamsVO sysParamsVO = new SysParamsVO();
        SysParams sysParams = sysParamsService.getSysParams();

        BeanUtils.copyProperties(sysParams, sysParamsVO);
        return GraceJSONResult.ok(sysParamsVO);
    }

}
