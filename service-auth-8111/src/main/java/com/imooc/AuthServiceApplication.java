package com.imooc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync    //  当前服务开启异步任务
@EnableRetry    //  当前服务开启重试机制
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.imooc.mapper")
@EnableFeignClients("com.imooc.api.feign")      //  当前服务是远程服务的调用方，需要是扫描 feign 客户端所在的包
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
