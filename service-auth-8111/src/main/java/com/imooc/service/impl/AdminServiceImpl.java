package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.mapper.AdminMapper;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.JWTUtils;
import com.imooc.utils.MD5Utils;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final AdminMapper adminMapper;

    private final JWTUtils jwtUtils;

    public AdminServiceImpl(AdminMapper adminMapper, JWTUtils jwtUtils) {
        this.adminMapper = adminMapper;
        this.jwtUtils = jwtUtils;
    }

    /**
     * admin 用户登录流程 包含密码加盐操作
     * 数据库中存储 admin 用户加密后的密码和盐值
     * 获取用户输入内容进行加密后 和数据库密码做比对
     *
     * @param adminBO
     * @return
     */
    @Override
    public boolean login(AdminBO adminBO) {

        //  根据用户名获取 数据库中存储的 admin 用户信息
//        Admin admin = adminMapper.selectOne(
//                new QueryWrapper<Admin>()
//                        .eq("username", adminBO.getUsername())
//        );
        Admin admin = getSelfAdmin(adminBO);
        //  对获取的用户进行判空
        if (admin == null)
            return false;

        //  对用户输入的内容进行加密操作
        String passwordMD5 = MD5Utils.encrypt(adminBO.getPassword(), admin.getSlat());
        //  将加密后的密码和数据库中存储的密码进行比对
        if (passwordMD5.equalsIgnoreCase(admin.getPassword())) {
            return true;
        }

        return false;
    }

    /**
     * 获取 admin 用户信息
     * @param adminBO
     * @return
     */
    @Override
    public Admin getAdminInfo(AdminBO adminBO) {

        return getSelfAdmin(adminBO);
    }

    /**
     * 获取 admin 用户信息
     * @param adminBO
     * @return
     */
    private Admin getSelfAdmin(AdminBO adminBO){
        Admin admin = adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username", adminBO.getUsername())
        );

        return admin;
    }
}
