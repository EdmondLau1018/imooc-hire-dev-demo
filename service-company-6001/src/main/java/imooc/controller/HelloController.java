package imooc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/c")
public class HelloController {

    @GetMapping("/hello")
    public String getHello() {
        return "Hello CompanyService ~~~";
    }
}
