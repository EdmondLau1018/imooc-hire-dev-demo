
package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.AdminMapper;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.JWTUtils;
import com.imooc.utils.MD5Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public AdminServiceImpl(AdminMapper adminMapper, JWTUtils jwtUtils) {
        this.adminMapper = adminMapper;
    }

    /**
     * admin 创建新用户 （分配账号）
     *
     * @param createAdminBO
     */
    @Override
    public void createAdmin(CreateAdminBO createAdminBO) {

        //  如果当前用户名存在 禁止创建账号
        Admin adminExist = adminMapper.selectOne(new QueryWrapper<Admin>()
                .eq("username", createAdminBO.getUsername()));
        if (adminExist != null)
            GraceException.displayException(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);

        //  创建新用户 写入admin 用户信息
        Admin newAdmin = new Admin();
        BeanUtils.copyProperties(createAdminBO,newAdmin);

        //  生成六位随机整数作为盐 salt
        String salt = (int) (Math.random() * 900000) + 100000 + "";
        //  对用户输入的密码进行加密
        String passwordMD5 = MD5Utils.encrypt(createAdminBO.getPassword(), salt);
        newAdmin.setPassword(passwordMD5);
        newAdmin.setSlat(salt);

        // 创建时间等其他信息
        newAdmin.setCreateTime(LocalDateTime.now());
        newAdmin.setUpdatedTime(LocalDateTime.now());

        adminMapper.insert(newAdmin);
    }
}
