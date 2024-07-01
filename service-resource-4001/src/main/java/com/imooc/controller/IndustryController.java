package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Industry;
import com.imooc.service.IndustryService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 获取行业节点列表 father_id 为 0 的所有节点
     *
     * @param request
     * @return
     */
    @GetMapping("/getTopList")
    public GraceJSONResult getTopList(HttpServletRequest request) {

        return GraceJSONResult.ok(industryService.getTopIndustryList());
    }

    /**
     * 根据行业 id 获取子节点
     * 行业id可以为 0 获取的就是行业根节点
     * @param topIndustryId
     * @return
     */
    @GetMapping("/children/{topIndustryId}")
    public GraceJSONResult getChildrenIndustryList(@PathVariable("topIndustryId") String topIndustryId) {

        return GraceJSONResult.ok(industryService.getChildrenIndustryList(topIndustryId));
    }

    /**
     * 更新行业节点信息
     * @param industry
     * @return
     */
    @PostMapping("/updateNode")
    public GraceJSONResult updateNode(@RequestBody Industry industry){

        industryService.updateNode(industry);
        return GraceJSONResult.ok();
    }
}
