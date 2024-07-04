package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Company;
import com.imooc.pojo.bo.CreateCompanyBO;
import com.imooc.pojo.vo.CompanySimpleVO;
import com.imooc.service.CompanyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * 根据公司全名获取公司信息
     *
     * @param fullName
     * @return
     */
    @PostMapping("/getByFullName")
    public GraceJSONResult getByFullName(String fullName) {

        if (StringUtils.isBlank(fullName))
            return GraceJSONResult.error();
        //  根据前端传递的公司全名进行 公司信息查询
        Company company = companyService.getByFullName(fullName);
        //  判断查询结果是否为空
        if (company == null)
            return GraceJSONResult.ok();

        //  信息拷贝（将查询出的信息去除敏感数据通过 vo 返回给前端）
        CompanySimpleVO companySimpleVO = new CompanySimpleVO();
        BeanUtils.copyProperties(company, companySimpleVO);

        return GraceJSONResult.ok(companySimpleVO);
    }

    /**\
     * app 端 创建公司和审核失败后重置公司信息接口
     * @param createCompanyBO
     * @return
     */
    @PostMapping("/createNewCompany")
    public GraceJSONResult createNewCompany(@RequestBody @Valid CreateCompanyBO createCompanyBO) {

        //  根据 参数是带有 companyid 判断用户是刚开始注册（新增 ）还是修改未审核通过的信息 修改
        String companyId = createCompanyBO.getCompanyId();
        String dbCompanyId = "";

        if (StringUtils.isBlank(companyId)) {
            //  当前用户之前 未提交公司审核信息 新增公司信息
            dbCompanyId = companyService.createNewCompany(createCompanyBO);
        } else {
            //  当前用户已提交 公司审核信息 修改公司信息
            dbCompanyId = companyService.resetCompanyReview(createCompanyBO);
        }

        return GraceJSONResult.ok(dbCompanyId);
    }
}
