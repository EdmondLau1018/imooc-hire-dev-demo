package com.imooc.api.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class MyThreadPool {

    /**
     * 初始化线程池，并且注入到 Spring 容器中
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {

        //  提问 初始化线程池 ，一般而言 设置的线程数，最大线程数 和线程存活时间 为多少 ？
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                3,
                10,
                45,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        return threadPoolExecutor;
    }
}
