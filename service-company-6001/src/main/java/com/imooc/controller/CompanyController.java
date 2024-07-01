package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Company;
import com.imooc.pojo.vo.CompanySimpleVO;
import com.imooc.service.CompanyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
