package com.imooc.test.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutor {

    /**
     *     public ThreadPoolExecutor(int corePoolSize,
     *                               int maximumPoolSize,
     *                               long keepAliveTime,
     *                               TimeUnit unit,
     *                               BlockingQueue<Runnable> workQueue,
     *                               ThreadFactory threadFactory,
     *                               RejectedExecutionHandler handler)
     */
    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3,
            10,
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(5000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
}
