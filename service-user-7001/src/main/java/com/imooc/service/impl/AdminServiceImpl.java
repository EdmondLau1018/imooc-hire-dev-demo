
package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.AdminMapper;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.JWTUtils;
import com.imooc.utils.MD5Utils;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
@Service
public class AdminServiceImpl extends BaseInfoProperties implements AdminService {

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
        BeanUtils.copyProperties(createAdminBO, newAdmin);

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

    /**
     * 针对 admin 用户信息的分页查询
     *
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PagedGridResult getAdminList(String accountName, Integer page, Integer limit) {

        //  一个帮助分页的拦截器 ，相当于在 sql 后面追加 对应的 分页 limit 参数
        PageHelper.startPage(page, limit);
        //  查询数据库中 账户名称相似的 admin 信息
        List<Admin> adminList = adminMapper.selectList(
                new QueryWrapper<Admin>()
                        .like("username", accountName));
        return setterPagedGrid(adminList, page);
    }

    /**
     * 根据 账户名称查找和删除管理员用户
     * @param username
     */
    @Override
    public void deleteAdmin(String username) {

        int rows = adminMapper.delete(
                new QueryWrapper<Admin>()
                        .eq("username", username)
                        //  ne 代表在 where 条件中添加不等于
                        .ne("username", "admin"));

        //  如果受影响的行数为 0 则删除失败
        if (rows == 0)
            GraceException.displayException(ResponseStatusEnum.ADMIN_DELETE_ERROR);
    }

    @Override
    public Admin getById(String id) {

        return adminMapper.selectById(id);
    }

    @Override
    public Admin getAdminInfoByName(String adminName) {

        return adminMapper.selectOne(new QueryWrapper<Admin>().eq("username",adminName));
    }

}
