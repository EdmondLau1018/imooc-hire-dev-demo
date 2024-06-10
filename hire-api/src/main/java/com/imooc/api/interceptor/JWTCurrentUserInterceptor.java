package com.imooc.api.interceptor;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.pojo.Admin;
import com.imooc.pojo.Users;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获得用户信息，将用户信息（分为 admin 和 user）放入 ThreadLocal 对象
 */
public class JWTCurrentUserInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    public static ThreadLocal<Users> currentUser = new ThreadLocal<>();

    public static ThreadLocal<Admin> adminUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //  从 header 中获取用户信息
        String appUserJson = request.getHeader(APP_USER_JSON);
        String saasUserJson = request.getHeader(SAAS_USER_JSON);
        String adminUserJson = request.getHeader(ADMIN_USER_JSON);

        // 将获取的用户信息存储到 ThreadLocal 存储空间中
        if (StringUtils.isNotBlank(appUserJson)) {
            Users appUser = new Gson().fromJson(appUserJson, Users.class);
            currentUser.set(appUser);
        }
        if (StringUtils.isNotBlank(adminUserJson)) {
            Admin admin = new Gson().fromJson(adminUserJson, Admin.class);
            adminUser.set(admin);
        }

        //  从 header 中获取 用户信息后放行所有请求
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 当前项目运行结束的时候 清空 ThreadLocal 存储空间
        currentUser.remove();
        adminUser.remove();
    }
}
