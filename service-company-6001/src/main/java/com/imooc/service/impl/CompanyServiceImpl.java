package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.CompanyReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.CompanyMapper;
import com.imooc.mapper.CompanyMapperCustom;
import com.imooc.pojo.Company;
import com.imooc.pojo.bo.CreateCompanyBO;
import com.imooc.pojo.bo.QueryCompanyBO;
import com.imooc.pojo.bo.ReviewCompanyBO;
import com.imooc.pojo.vo.CompanyInfoVO;
import com.imooc.service.CompanyService;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 企业表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class CompanyServiceImpl extends BaseInfoProperties implements CompanyService {

    private final CompanyMapper companyMapper;

    private final CompanyMapperCustom companyMapperCustom;

    public CompanyServiceImpl(CompanyMapper companyMapper, CompanyMapperCustom companyMapperCustom) {
        this.companyMapper = companyMapper;
        this.companyMapperCustom = companyMapperCustom;
    }

    @Override
    public Company getByFullName(String fullName) {

        Company company = companyMapper.selectOne(
                new QueryWrapper<Company>()
                        .eq("company_name", fullName)
        );
        return company;
    }

    /**
     * 创建新的公司信息 提交审核 实现方法
     *
     * @param createCompanyBO
     * @return
     */
    @Transactional
    @Override
    public String createNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();
        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setIsVip(YesOrNo.NO.type);
        //   将公司的审核状态属性设置为  未审核
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        //  设置时间属性
        newCompany.setCreatedTime(LocalDateTime.now());
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.insert(newCompany);
        return newCompany.getId();
    }

    /**
     * 更新公司信息 创建的公司信息审核未通过
     *
     * @param createCompanyBO
     * @return
     */
    @Transactional
    @Override
    public String resetCompanyReview(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();
        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setId(createCompanyBO.getCompanyId());
        newCompany.setIsVip(YesOrNo.NO.type);
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(newCompany);

        return createCompanyBO.getCompanyId();
    }

    /**
     * 根据企业 id 查询 企业信息 实现方法
     *
     * @param companyId
     * @return
     */
    @Override
    public Company getById(String companyId) {
        return companyMapper.selectById(companyId);
    }

    /**
     * 更新待审核的公司信息实现方法
     *
     * @param reviewCompanyBO
     */
    @Transactional
    @Override
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());
        pendingCompany.setReviewStatus(reviewCompanyBO.getReviewStatus());
        //  如果上次的审核信息未通过 （重置审核信息）
        pendingCompany.setReviewReplay("");
        pendingCompany.setAuthLetter(reviewCompanyBO.getAuthLetter());

        pendingCompany.setCommitUserId(reviewCompanyBO.getHrUserId());
        pendingCompany.setCommitUserMobile(reviewCompanyBO.getHrMobile());
        pendingCompany.setCommitDate(LocalDate.now());

        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }

    @Override
    public PagedGridResult queryCompanyListPaged(QueryCompanyBO queryCompanyBO, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        //  构建查询参数
        HashMap<String, Object> map = new HashMap<>();
        map.put("companyName", queryCompanyBO.getCompanyName());
        map.put("commitUser", queryCompanyBO.getCommitUser());
        map.put("reviewStatus", queryCompanyBO.getReviewStatus());
        map.put("commitDateStart", queryCompanyBO.getCommitDateStart());
        map.put("commitDateEnd", queryCompanyBO.getCommitDateEnd());

        List<CompanyInfoVO> companyList = companyMapperCustom.queryCompanyList(map);

        return setterPagedGrid(companyList, page);
    }

    /**
     * 根据公司 id 查询企业基本信息
     *
     * @param companyId
     * @return
     */
    @Override
    public CompanyInfoVO queryCompanyInfo(String companyId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("companyId", companyId);

        //  持久层查询公司信息
        CompanyInfoVO companyInfo = companyMapperCustom.queryCompanyInfo(map);
        return companyInfo;
    }

    /**
     * 更新审核后的企业信息
     * 更新的字段是审核的状态和审核信息
     * @param reviewCompanyBO
     */
    @Transactional
    @Override
    public void updateReviewInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());
        pendingCompany.setReviewStatus(reviewCompanyBO.getReviewStatus());
        pendingCompany.setReviewReplay(reviewCompanyBO.getReviewReplay());
        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }
}
