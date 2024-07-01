package com.imooc.service;

import com.imooc.pojo.Company;

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

}
