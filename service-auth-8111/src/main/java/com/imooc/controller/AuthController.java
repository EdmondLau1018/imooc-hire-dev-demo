package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/a")
public class AuthController {
    @GetMapping("/testAuth")
    public GraceJSONResult sendOK(){
        log.info("auth-service 认证服务正常");
        return GraceJSONResult.ok();
    }
}
