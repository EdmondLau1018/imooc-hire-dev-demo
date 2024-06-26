package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/f")
@RestController
public class HelloServiceController {

    @GetMapping("/hello")
    public GraceJSONResult sayHi(){
        return GraceJSONResult.ok("hello file-service~~~");
    }
}
