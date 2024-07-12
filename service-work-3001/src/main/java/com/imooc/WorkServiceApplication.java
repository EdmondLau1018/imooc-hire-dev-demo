package com.imooc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan(basePackages = "com.imooc.mapper")
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class WorkServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkServiceApplication.class, args);
    }
}
