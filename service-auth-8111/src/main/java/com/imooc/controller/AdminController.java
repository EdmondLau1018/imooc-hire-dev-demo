package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.interceptor.JWTCurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.vo.AdminVO;
import com.imooc.service.AdminService;
import com.imooc.utils.JWTUtils;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseInfoProperties {

    private final AdminService adminService;

    private final JWTUtils jwtUtils;

    public AdminController(AdminService adminService, JWTUtils jwtUtils) {
        this.adminService = adminService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Admin 用户登录接口
     *
     * @return
     */
    @PostMapping("/login")
    public GraceJSONResult login(@RequestBody AdminBO adminBO) {

        //  检查当前 admin 用户是否存在
        boolean isExist = adminService.login(adminBO);
        if (!isExist)
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_LOGIN_ERROR);

        //  admin 用户存在 证明 登录验证成功  根据 admin 用户信息生成 jwt
        Admin admin = adminService.getAdminInfo(adminBO);
        String adminToken = jwtUtils.createJWTWithPrefix(TOKEN_ADMIN_PREFIX, new Gson().toJson(admin), Long.valueOf(8 * 60 * 60 * 1000));

        //  返回生成的 admin Token 信息
        return GraceJSONResult.ok(adminToken);
    }

    /**
     * 从 threadLocal 对象中获取当前已经
     * 登录的admin 用户信息
     *
     * @return
     */
    @GetMapping("/info")
    public GraceJSONResult info(String token) {
        //  从 当前项目的 ThreadLocal 对象中获取已经登陆的 admin 用户信息
        Admin admin = JWTCurrentUserInterceptor.adminUser.get();
        //  创建 vo 对象用来返回信息
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);
        return GraceJSONResult.ok(adminVO);
    }

    /**
     * 实现用户退出登录的功能
     *
     * @return
     */
    @PostMapping("/logout")
    public GraceJSONResult logout() {

        return GraceJSONResult.ok();
    }

}
