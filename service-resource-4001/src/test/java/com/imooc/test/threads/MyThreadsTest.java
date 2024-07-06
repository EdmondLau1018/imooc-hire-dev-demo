package com.imooc.test.threads;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MyThreadsTest {

    @Test
    public void testThreadClass() {

        System.out.println("开始测试...");
        ThreadClass_01 threadClass_01 = new ThreadClass_01();
        ThreadClass_02 threadClass_02 = new ThreadClass_02();

        threadClass_01.start();
        threadClass_02.start();
        System.out.println("测试结束...");
    }

    @Test
    public void testRunnableClass() {

        System.out.println("开始测试...");
        RunnableClass_01 runnableClass_01 = new RunnableClass_01();
        RunnableClass_02 runnableClass_02 = new RunnableClass_02();

        new Thread(runnableClass_01).start();
        new Thread(runnableClass_02).start();
        System.out.println("测试结束...");
    }

    @Test
    public void testCallableClass() throws Exception {

        System.out.println("开始测试...");
        FutureTask<Object> futureTask01 = new FutureTask<>(new CallableClass_01());
        FutureTask<Object> futureTask02 = new FutureTask<>(new CallableClass_02());

        new Thread(futureTask01).start();
        new Thread(futureTask02).start();

        System.out.println("主线程获取 futureTask 01 结果：" + futureTask01.get());
        System.out.println("主线程获取 futureTask 02 结果：" + futureTask02.get());

        System.out.println("测试结束...");
    }
}
