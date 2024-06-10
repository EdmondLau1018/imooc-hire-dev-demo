package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.interceptor.JWTCurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/a")
public class AuthController extends BaseInfoProperties {
    @GetMapping("/testAuth")
    public GraceJSONResult sendOK(HttpServletRequest request) {
        log.info("auth-service 认证服务正常");
//        String userJson = request.getHeader(APP_USER_JSON);
//        Users users = new Gson().fromJson(userJson, Users.class);
//        log.info("AuthController --- testAuth --- 用户信息：{}",users.toString());

        Users currentUser = JWTCurrentUserInterceptor.currentUser.get();

        log.info("认证服务获取的当前用户信息：{}", currentUser);
        return GraceJSONResult.ok();
    }
}
