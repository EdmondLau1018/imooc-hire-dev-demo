package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Industry;
import com.imooc.service.IndustryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/industry")
public class IndustryController extends BaseInfoProperties {

    private final IndustryService industryService;

    public IndustryController(IndustryService industryService) {
        this.industryService = industryService;
    }

    /**
     * 创建行业节点
     *
     * @param industry
     * @return
     */
    @PostMapping("/createNode")
    public GraceJSONResult createNode(@RequestBody Industry industry) {

        //  判断节点名称是否存在 (行业存在的情况 返回 行业已存在的结果)
        if (!industryService.getIndustryIsExistByName(industry.getName()))
            return GraceJSONResult.errorMsg("该行业已存在，请重新取名~~~~");

        //  行业不存在 创建 行业根节点
        industryService.createNode(industry);
        return GraceJSONResult.ok();
    }


}
