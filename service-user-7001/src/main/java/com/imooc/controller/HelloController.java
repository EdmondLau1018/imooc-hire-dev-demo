package com.imooc.controller;

import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/u")
public class HelloController {

    private final StuService stuService;

    private final SMSUtils smsUtils;

    public HelloController(StuService stuService, SMSUtils smsUtils) {
        this.stuService = stuService;
        this.smsUtils = smsUtils;
    }

    @GetMapping("/hello")
    public String getHello() {

        return "Hello UserService ~~~" ;
    }

    @GetMapping("/testResult")
    public GraceJSONResult testResult(){
        return GraceJSONResult.ok();
    }

    @PostMapping("/testAdd")
    public GraceJSONResult testAdd(){
        Stu stu = new Stu();
        stu.setName("imooc测试");
        stu.setAge(23);
        stuService.save(stu);
        return GraceJSONResult.ok();
    }

    @PostMapping("/testSMS")
    public GraceJSONResult testSMS(){
        try {
            smsUtils.sendSMS("15162295312","2233");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GraceJSONResult.ok();
    }
}
