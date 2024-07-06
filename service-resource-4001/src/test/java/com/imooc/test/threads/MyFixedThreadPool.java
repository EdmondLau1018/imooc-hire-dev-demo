package com.imooc.test.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyFixedThreadPool {

    //  固定线程数量的 线程池
    public static ExecutorService executorService = Executors.newFixedThreadPool(3);

}
