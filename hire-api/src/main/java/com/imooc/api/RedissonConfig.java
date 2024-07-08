package com.imooc.api;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    /**
     * 初始化 Redisson 客户端
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.32.100:6379")
                .setPassword("1018")
                .setDatabase(0)                         //  设置 redis 使用的数据库
                .setConnectionMinimumIdleSize(10)       // 设置最小空闲连接数
                .setConnectionPoolSize(20)              //  设置连接池的大小
                .setIdleConnectionTimeout(60 * 1000)    //  销毁超时最大连接数，当连接数超过最小连接数且超时的时候就会被销毁
                .setConnectTimeout(15 * 1000)           //  客户端 获得 redis 连接的响应超时时间
                .setTimeout(15 * 1000);                  //  响应的超时时间

        return Redisson.create(config);
    }
}
