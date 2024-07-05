package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.feign.UserMicroServiceFeign;
import com.imooc.api.interceptor.JWTCurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.CompanyReviewStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Company;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CreateCompanyBO;
import com.imooc.pojo.bo.QueryCompanyBO;
import com.imooc.pojo.bo.ReviewCompanyBO;
import com.imooc.pojo.vo.CompanyInfoVO;
import com.imooc.pojo.vo.CompanySimpleVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.CompanyService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.PagedGridResult;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPSize;
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
     *
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
    /**************************************************业务分割：SAAS端*************************************************/

    /**
     * saas 管理端 主页获取企业信息
     *
     * @return
     */
    @PostMapping("/info")
    public GraceJSONResult info() {

        //  获取当前登录的普通用户
        Users currentUser = JWTCurrentUserInterceptor.currentUser.get();
        //  根据当前登录的 HR 关联的公司 id 获取企业信息
        CompanySimpleVO company = getCompany(currentUser.getHrInWhichCompanyId());

        return GraceJSONResult.ok(company);
    }

    /**
     * 根据当前用户 所在 公司  id 查询公司详细信息
     * @return
     */
    @PostMapping("/saas/moreInfo")
    public GraceJSONResult moreInfoSaas() {

        //  获取当前用户
        Users currentUser = JWTCurrentUserInterceptor.currentUser.get();
        CompanyInfoVO companyMoreInfo = getCompanyMoreInfo(currentUser.getHrInWhichCompanyId());

        return GraceJSONResult.ok(companyMoreInfo);
    }

    /**
     * saas 管理端 根据企业 id 从缓存和 DB 中查询对应的企业信息（单个）
     *
     * @param companyId
     * @return
     */
    public CompanyInfoVO getCompanyMoreInfo(String companyId) {

        if (StringUtils.isBlank(companyId))
            return null;

        //  从 redis 缓存中查询 公司的详细信息
        String companyJson = redis.get(REDIS_COMPANY_MORE_INFO + ":" + companyId);
        //  如果结果为空 查询数据库
        if (StringUtils.isBlank(companyJson)) {

            Company company = companyService.getById(companyId);
            CompanyInfoVO companyInfoVO = new CompanyInfoVO();
            BeanUtils.copyProperties(company, companyInfoVO);
            companyInfoVO.setCompanyId(companyId);

            //  将查询的结果设置到 缓存中
            redis.set(REDIS_COMPANY_MORE_INFO + ":" + companyId, new Gson().toJson(companyInfoVO), 3 * 60);

            return companyInfoVO;
        } else {
            //  缓存中存在当前企业的缓存  直接转换成对象并返回
            return new Gson().fromJson(companyJson, CompanyInfoVO.class);
        }
    }

    /**************************************************业务分割：运营管理端*************************************************/

    /**
     * 运营管理端公司列表分页查询
     *
     * @param queryCompanyBO
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("/admin/getCompanyList")
    public GraceJSONResult getCompanyList(@RequestBody @Valid QueryCompanyBO queryCompanyBO,
                                          Integer page,
                                          Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        //  获得运营端公司列表的分类查询结果
        PagedGridResult gridResult = companyService.queryCompanyListPaged(queryCompanyBO,
                page,
                limit);

        return GraceJSONResult.ok(gridResult);
    }

    /**
     * 根据公司  id 获取具体的 公司信息
     *
     * @param companyId
     * @return
     */
    @PostMapping("/admin/getCompanyInfo")
    public GraceJSONResult getCompanyInfo(String companyId) {

        CompanyInfoVO companyInfo = companyService.queryCompanyInfo(companyId);

        return GraceJSONResult.ok(companyInfo);
    }

    /**
     * 更新企业审核信息
     * 远程调用用户服务 修改用户身份为招聘者
     *
     * @param reviewCompanyBO
     * @return
     */
    @PostMapping("/admin/doReview")
    public GraceJSONResult doReview(@RequestBody ReviewCompanyBO reviewCompanyBO) {

        //  参数校验 企业 id 和用户 id 都不能为空
        String companyId = reviewCompanyBO.getCompanyId();
        String hrUserId = reviewCompanyBO.getHrUserId();
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(hrUserId))
            return GraceJSONResult.error();

        //  审核企业信息
        companyService.updateReviewInfo(reviewCompanyBO);

        //  审核企业成功 （审核状态为 成功 ）审核企业信息未发生异常 修改用户的身份信息 为 HR
        if (reviewCompanyBO.getReviewStatus() == CompanyReviewStatus.SUCCESSFUL.type) {
            //  将当前 提交企业审核的用户 身份设置为 HR
            userMicroServiceFeign.changeUserToHR(hrUserId);
        }

        //  （企业信息发生变更） 清除 企业缓存
        redis.del(REDIS_COMPANY_BASE_INFO + ":" + companyId);

        return GraceJSONResult.ok();

    }
}
