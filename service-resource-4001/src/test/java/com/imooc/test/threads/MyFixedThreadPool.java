package com.imooc.test.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyFixedThreadPool {

    //  固定线程数量的 线程池
    public static ExecutorService executorService = Executors.newFixedThreadPool(3);

    //  缓存线程池 创建的线程都会被回收
    public static ExecutorService executorServiceCached = Executors.newCachedThreadPool();

    //  单线程线程池 单线程从队列中获取任务并执行
    public static ExecutorService executorServiceSingle = Executors.newSingleThreadExecutor();

    //  定时任务线程池
    public static ExecutorService executorServiceSchedule = Executors.newScheduledThreadPool(3);

}
