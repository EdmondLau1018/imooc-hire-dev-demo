package com.imooc.service;

import com.imooc.pojo.Company;
import com.imooc.pojo.bo.*;
import com.imooc.pojo.vo.CompanyInfoVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

/**
 * <p>
 * 企业表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface CompanyService {

    /**
     * 根据公司全名获取公司的具体数据
     * 条件查询 eq
     *
     * @param fullName
     * @return
     */
    public Company getByFullName(String fullName);

    /**
     * 新建公司（公司未创建） 返回创建后的公司 id
     *
     * @param createCompanyBO
     * @return
     */
    public String createNewCompany(CreateCompanyBO createCompanyBO);

    /**
     * 更新公司信息
     *
     * @param createCompanyBO
     * @return
     */
    public String resetCompanyReview(CreateCompanyBO createCompanyBO);

    /**
     * 根据公司 id 查询企业信息
     *
     * @param companyId
     * @return
     */
    public Company getById(String companyId);

    /**
     * 更新待审核的公司信息
     *
     * @param reviewCompanyBO
     */
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO);

    /**
     * 运营平台 查询企业提交审核列表
     * 分页查询
     * @param queryCompanyBO
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryCompanyListPaged(QueryCompanyBO queryCompanyBO, Integer page, Integer pageSize);

    /**
     * 根据公司 id 查询企业具体信息
     * @param companyId
     * @return
     */
    public CompanyInfoVO queryCompanyInfo(String companyId);

    /**
     * 更新审核后的企业信息
     * @param reviewCompanyBO
     */
    public void updateReviewInfo(ReviewCompanyBO reviewCompanyBO);

    /**
     * app 端修改 企业信息接口
     * @param modifyCompanyInfoBO
     */
    public void modifyCompanyInfo(ModifyCompanyInfoBO modifyCompanyInfoBO) throws InterruptedException;

    /**
     * 修改 企业的相册信息
     * 操作的是企业相册关联表
     * @param companyInfoBO
     */
    public void savePhotos(ModifyCompanyInfoBO companyInfoBO);

    public void modifyCompanyInfoFairLock(ModifyCompanyInfoBO modifyCompanyInfoBO);

    public void modifyCompanyInfoMultiLock(ModifyCompanyInfoBO modifyCompanyInfoBO);

//    public void testReadLock(INt);

    /**
     * 高并发请求测试读写锁
     */
    public void testReadLock();

    public void testWriteLock();

    /**
     * 信号量的获取和释放
     * @param num
     */
    public void getSemaphore(Integer num);

    public void releaseSemaphore(Integer num);

    /**
     * 闭锁资源声明与消耗测试
     * 通常用于某些具体流程中
     */
    public void releaseCar();

    public void doneStepCar(String name);

    /**
     * 根据公司 id 列表查询公司信息
     * @param searchBO
     * @return
     */
    public List<Company> getList(SearchBO searchBO);
}
