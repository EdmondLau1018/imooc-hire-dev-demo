package com.imooc.test.threads;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

    @Test
    public void ThreadPoolTest() throws Exception {

        // 在线程池中创建线程执行的任务
        Future<Object> futureTask = MyFixedThreadPool
                .executorService
                .submit(new CallableClass_01());

        //  获取线程执行完毕后的结果
        System.out.println("线程池任务执行结果： " + futureTask.get());
    }

    @Test
    public void CompletableFutureTest() {

        System.out.println("开始运行 test ...");

        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("第一个参数是编排线程的任务内容，第二个参数是使用的线程池~~~");
        }, MyFixedThreadPool.executorService);

        System.out.println("结束运行 test ...");
    }

    /**
     * 测试有返回的 编排任务
     */
    @Test
    public void testCompletableFuture() throws Exception {

        System.out.println("开始运行 test ...");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {

            String uuid = UUID.randomUUID().toString();
            System.out.println("带返回的编排任务 uuid 内部打印： " + uuid);
            return uuid;
        }, MyThreadPoolExecutor.threadPool);

        //  这里使用 get 会导致主线程阻塞
        System.out.println("线程外部获取的结果为： " + future.get());

        System.out.println("结束运行 test ...");
    }

    @Test
    public void testComplete() throws Exception {
        System.out.println("开始运行 test ...");

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {

            String uuid = UUID.randomUUID().toString();
            System.out.println("当前线程编号为：" + Thread.currentThread().getId() + "   内部打印 uuid :" + uuid);
            return uuid;
        }).whenComplete((s, throwable) -> {

            System.out.println("线程任务执行完毕后获取的结果为 :" + s);
        }).exceptionally((throwable) -> {

            System.out.println("进入异常兜底方法 ，异常的信息为：" + throwable.getMessage());
            //   返回一个新的 uuid
            return UUID.randomUUID().toString();
        });

        System.out.println("线程任务执行完毕，获得的 uuid 外部打印 ："  + completableFuture.get());

        System.out.println("结束运行 test ...");
    }
}
