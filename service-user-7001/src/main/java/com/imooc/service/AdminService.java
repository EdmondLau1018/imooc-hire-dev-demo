package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.base.BaseInfoProperties;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.utils.PagedGridResult;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
public interface AdminService {

    /**
     * 创建 admin 用户
     * @param createAdminBO
     */
    public void createAdmin(CreateAdminBO createAdminBO);

    /**
     * 根据账号名称模糊查询账号信息列表
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult getAdminList(String accountName,Integer page,Integer limit);

    /**
     * 根据账号名称 查找和删除admin用户
     * @param username
     */
    public void deleteAdmin(String username);

    public Admin getById(String id);

    public Admin getAdminInfoByName(String adminName);
}
