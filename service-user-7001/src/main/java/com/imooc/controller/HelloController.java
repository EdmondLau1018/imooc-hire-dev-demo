package com.imooc.controller;

import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import grace.result.GraceJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/u")
public class HelloController {

    private final StuService stuService;

    public HelloController(StuService stuService) {
        this.stuService = stuService;
    }

    @GetMapping("/hello")
    public String getHello() {

        return "Hello UserService ~~~" ;
    }

    @GetMapping("/testResult")
    public GraceJSONResult testResult(){
        return GraceJSONResult.ok();
    }

    @GetMapping("/testAdd")
    public GraceJSONResult testAdd(){
        Stu stu = new Stu();
        stu.setName("imooc测试");
        stu.setAge(23);
        stuService.save(stu);
        return GraceJSONResult.ok();
    }
}
