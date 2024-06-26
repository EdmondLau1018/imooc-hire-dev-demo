package com.imooc;

import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 文件服务启动类
 * 排除数据源依赖自动注入
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        SeataAutoConfiguration.class
})
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
}
