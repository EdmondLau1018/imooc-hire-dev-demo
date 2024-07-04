package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.enums.CompanyReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.CompanyMapper;
import com.imooc.pojo.Company;
import com.imooc.pojo.bo.CreateCompanyBO;
import com.imooc.pojo.bo.ReviewCompanyBO;
import com.imooc.service.CompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
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
     * @param companyId
     * @return
     */
    @Override
    public Company getById(String companyId) {
        return companyMapper.selectById(companyId);
    }

    /**
     * 更新待审核的公司信息实现方法
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
}
