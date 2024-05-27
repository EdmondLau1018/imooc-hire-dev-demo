package com.imooc.controller;

import grace.result.GraceJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojo.Stu;

@Slf4j
@RestController
@RequestMapping("/u")
public class HelloController {

    @GetMapping("/hello")
    public String getHello() {

        Stu stu = new Stu(101, "dumenghan", "bitch");
        log.info("新建的学生信息： {}",stu);
        return "Hello UserService ~~~" + stu;
    }

    @GetMapping("/testResult")
    public GraceJSONResult testResult(){
        return GraceJSONResult.ok( new Stu(1002,"爱迪生","女性"));
    }
}
