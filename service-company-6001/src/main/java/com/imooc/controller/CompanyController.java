package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.feign.UserMicroServiceFeign;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Company;
import com.imooc.pojo.bo.CreateCompanyBO;
import com.imooc.pojo.bo.ReviewCompanyBO;
import com.imooc.pojo.vo.CompanySimpleVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.CompanyService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/company")
public class CompanyController extends BaseInfoProperties {

    private final CompanyService companyService;

    private final UserMicroServiceFeign userMicroServiceFeign;

    public CompanyController(CompanyService companyService, UserMicroServiceFeign userMicroServiceFeign) {
        this.companyService = companyService;
        this.userMicroServiceFeign = userMicroServiceFeign;
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

    /**
     * \
     * app 端 创建公司和审核失败后重置公司信息接口
     *
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

    /**
     * 查询企业基本信息
     *
     * @param companyId
     * @param withHRCounts 是否需要查询 绑定的 HR 数量
     * @return
     */
    @PostMapping("/getInfo")
    public GraceJSONResult getInfo(String companyId, boolean withHRCounts) {

        CompanySimpleVO company = getCompany(companyId);

        //  如果需要查询 HR 绑定的数量 通过 feign 远程调用进行查询
        if (withHRCounts) {

            GraceJSONResult graceJSONResult = userMicroServiceFeign.getCountsByCompanyId(companyId);

            Object data = graceJSONResult.getData();
            Long hrCounts = Long.valueOf(data.toString());
            company.setHrCounts(hrCounts);
        }

        return GraceJSONResult.ok(company);
    }

    /**
     * 根据 companyId 获得企业的公用（基本信息）
     *
     * @param companyId
     * @return
     */
    private CompanySimpleVO getCompany(String companyId) {

        if (StringUtils.isBlank(companyId))
            return null;

        //  查询 redis 缓存 查询对应公司的基本信息
        String companyJson = redis.get(REDIS_COMPANY_BASE_INFO + ":" + companyId);
        if (StringUtils.isBlank(companyJson)) {
            //  当前企业信息在缓存中为空 查询数据库
            Company company = companyService.getById(companyId);
            //  如果发现请求携带的 company id 不存在 返回空对象
            if (company == null) return null;

            //  将查询出的 company 基本信息设置到缓存中
            CompanySimpleVO companySimpleVO = new CompanySimpleVO();
            BeanUtils.copyProperties(company, companySimpleVO);

            redis.set(REDIS_COMPANY_BASE_INFO + ":" + companyId,
                    GsonUtils.object2String(companySimpleVO),
                    5 * 60 * 60);

            return companySimpleVO;
        } else {
            // 缓存中的 企业信息不为空，解析企业信息 返回
            return new Gson().fromJson(companyJson, CompanySimpleVO.class);
        }
    }

    /**
     * 绑定 hr 用户和企业关系的接口
     *
     * @param reviewCompanyBO
     * @return
     */
    @PostMapping("/goReviewCompany")
    public GraceJSONResult goReviewCompany(@RequestBody @Valid ReviewCompanyBO reviewCompanyBO) {

        GraceJSONResult graceJSONResult = userMicroServiceFeign.bindingHRToCompany(
                reviewCompanyBO.getHrUserId(),
                reviewCompanyBO.getRealname(),
                reviewCompanyBO.getCompanyId()
        );

        String mobile = graceJSONResult.getData().toString();

        //  保存审核信息 更改公司信息的审核状态
        reviewCompanyBO.setHrMobile(mobile);
        companyService.commitReviewCompanyInfo(reviewCompanyBO);

        return GraceJSONResult.ok();
    }

    /**
     * 重新获取 hr 用户信息
     * 查询当前重新获取的用户对应的企业信息
     * @param hrUserId
     * @return
     */
    @PostMapping("/information")
    public GraceJSONResult information(String hrUserId) {

        UsersVO hrUserInfoVO = getHRUserInfoVO(hrUserId);

        CompanySimpleVO company = getCompany(hrUserInfoVO.getHrInWhichCompanyId());

        return GraceJSONResult.ok(company);
    }

    /**
     * 获取 hr 用户信息（远程接口调用）
     *
     * @param hrUserId
     * @return
     */
    public UsersVO getHRUserInfoVO(String hrUserId) {

        GraceJSONResult graceJSONResult = userMicroServiceFeign.get(hrUserId);

        //  微服务远程调用结果格式类型转换
        Object data = graceJSONResult.getData();
        String jsonString = JsonUtils.objectToJson(data);
        UsersVO hrUser = JsonUtils.jsonToPojo(jsonString, UsersVO.class);

        return hrUser;
    }
}
