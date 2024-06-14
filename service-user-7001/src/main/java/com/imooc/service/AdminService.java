package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
public interface AdminService extends IService<Admin> {

    public void createAdmin(CreateAdminBO createAdminBO);

}
