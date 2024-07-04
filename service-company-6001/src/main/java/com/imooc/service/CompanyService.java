package com.imooc.service;

import com.imooc.pojo.Company;
import com.imooc.pojo.bo.CreateCompanyBO;

/**
 * <p>
 * 企业表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface CompanyService{

    /**
     * 根据公司全名获取公司的具体数据
     * 条件查询 eq
     * @param fullName
     * @return
     */
    public Company getByFullName(String fullName);

    /**
     * 新建公司（公司未创建） 返回创建后的公司 id
     * @param createCompanyBO
     * @return
     */
    public String createNewCompany(CreateCompanyBO createCompanyBO);

    /**
     * 更新公司信息
     * @param createCompanyBO
     * @return
     */
    public String resetCompanyReview(CreateCompanyBO createCompanyBO);
}
