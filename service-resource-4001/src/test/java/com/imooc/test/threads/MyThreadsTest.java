package com.imooc.test.threads;

import org.junit.jupiter.api.Test;

import javax.swing.text.TabExpander;
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
//            return; whenComplete 没有返回结果
        }).exceptionally((throwable) -> {

            System.out.println("进入异常兜底方法 ，异常的信息为：" + throwable.getMessage());
            //   返回一个新的 uuid
            return UUID.randomUUID().toString();
        });

        //  在这里获取的是 业务方法 的返回 uuid
        System.out.println("线程任务执行完毕，获得的 uuid 外部打印 ：" + completableFuture.get());

        System.out.println("结束运行 test ...");
    }

    @Test
    public void testCompletable2() throws ExecutionException, InterruptedException {

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            System.out.println("生成的 uuid 内部打印 ： " + uuid);
            return uuid;
        }, MyThreadPoolExecutor.threadPool).handle((s, throwable) -> {
            //   s 指的是上个方法执行之后的返回结果
            System.out.println("从上一个方法中获取的 uuid :" + s);
            return "这是一个 String 类型的返回结果";
        });

        System.out.println("执行结果外部打印 -》》》》》》》》》》 ：" + completableFuture.get());

    }

    /************************************ 异步任务 顺序执行  ****************************************/
    @Test
    public void testCompletableFutureThen() throws Exception {

        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            System.out.println("当前线程编号为：" + Thread.currentThread().getId() + "   内部打印 uuid :" + uuid);
            return uuid;
        }).thenRun(() -> {
            String uuid = UUID.randomUUID().toString();
            System.out.println("在这里生成新的 uuid 不接收参数 ..." + uuid);
        }).thenRun(() -> {
            System.out.println("这是顺序执行的第三个方法");
        });

        System.out.println(future.get());

    }

    @Test
    public void testThen() throws ExecutionException, InterruptedException {

        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {

            String uuid = UUID.randomUUID().toString();
            System.out.println("supplyAsync --- 第一个方法执行生成的 UUID ：" + uuid);
            return uuid;
        }, MyThreadPoolExecutor.threadPool).thenApply((s) -> {
            //  只接收一个参数 并且提供返回值
            System.out.println("apply --- 接收到的参数 ： " + s);
            return s;
        }).thenAccept((s) -> {
            // 只接收一个参数 不提供返回值
            System.out.println("accept --- 接收到的参数： " + s);
        }).thenRun(() -> {
            System.out.println("不接收参数 也不提供返回值");
        });

        System.out.println(completableFuture.get());
    }

    /************************************ 双重任务 组合  ****************************************/
    @Test
    public void testCompletableRunAfterBoth() throws Exception {

        //  定义一个新的无返回值的任务
        CompletableFuture<Void> completableFuture1 = CompletableFuture
                .runAsync(new RunnableClass_01(),
                        MyThreadPoolExecutor.threadPool);

        CompletableFuture<Void> completableFutur2 = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            System.out.println("多线程异步任务生成随机 uuid： " + uuid);
            return uuid;
        }, MyThreadPoolExecutor.threadPool).runAfterBoth(completableFuture1, () -> {
            //runAfterBoth 第一个参数是 无返回值的任务对象
            System.out.println("两个任务都执行完了，但是当前任务不接收参数");
        });

        //  应该什么都获取不到
        System.out.println(completableFutur2.get());
    }

    /**
     * runAfterEither 其中一个任务优先完成
     * 无返回值
     */
    @Test
    public void testCompletableFutureRunAfterEither() throws Exception {

        //  定义一个新的无返回值的任务
        CompletableFuture<Void> completableFuture1 = CompletableFuture
                .runAsync(new RunnableClass_01(),
                        MyThreadPoolExecutor.threadPool);

        CompletableFuture<Void> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            System.out.println("多线程异步任务生成随机 uuid： " + uuid);
            return uuid;
        }, MyThreadPoolExecutor.threadPool).runAfterEither(completableFuture1, () -> {
            System.out.println("其中有一个任务优先执行完了.....");
        });

//        completableFuture2.get();
    }

    /**
     * 两个任务都执行完了 接收两个参数
     * 没有返回值
     */
    @Test
    public void testThenAcceptBoth() throws ExecutionException, InterruptedException {

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            return "123";
        }, MyThreadPoolExecutor.threadPool);

        CompletableFuture<Void> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            return "S2  :" + uuid;
        }, MyThreadPoolExecutor.threadPool).thenAcceptBoth(completableFuture1, (s, aVoid) -> {
            System.out.println("......." + s);
            //  参数是 一个 completable 对象和一个 lambda 表达式
            //  可以通过 completable 对象 那个任务对象的返回值
            try {
                System.out.println("应该是这么接收第一个方法的参数的： " + completableFuture1.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(completableFuture2.get());
    }

    /**
     * 两个任务都执行完之后才执行  可以获得两个参数 有返回值
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testThenCombine() throws ExecutionException, InterruptedException {

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            return "测试 123";
        }, MyThreadPoolExecutor.threadPool);

        CompletableFuture<Object> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            return "测试 567";
        }, MyThreadPoolExecutor.threadPool).thenCombine(completableFuture1, (s, aVoid) -> {
            String s1 = null;
            System.out.println("s2 =========" + s);
            try {
                s1 = completableFuture1.get();
                System.out.println("s1 =========== " + s1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return s + " ||||||||" + s1;
        });

        System.out.println(completableFuture2.get());
    }

    @Test
    public void testCompletableFutureThenCombine() throws Exception {

        //  定义一个新的无返回值的任务
        CompletableFuture<String> completableFuture1 = CompletableFuture
                .supplyAsync(() -> {
                    String uuid = UUID.randomUUID().toString();
                    System.out.println("多线程异步任务生成随机 uuid： " + uuid);
                    return uuid;
                }, MyThreadPoolExecutor.threadPool);

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            String uuid = UUID.randomUUID().toString();
            System.out.println("多线程异步任务生成随机 uuid： " + uuid);
            return uuid;
        }, MyThreadPoolExecutor.threadPool).thenCombine(completableFuture1, (s1, s2) -> {

            System.out.println("两个异步任务都执行完了，他们的结果分别为：");
            System.out.println(s1);
            System.out.println(s2);

            return s1 + s2;
        });

        System.out.println("组合之后的结果：" + completableFuture2.get());
    }


    /************************************ 多重任务 组合  ****************************************/
    @Test
    public void testCompletableAllOf() throws Exception {

        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(new RunnableClass_01(), MyThreadPoolExecutor.threadPool);
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(new RunnableClass_02(), MyThreadPoolExecutor.threadPool);

        CompletableFuture<String> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            String res = "这是第三个定义的任务，执行这个的结果。。。";
            System.out.println(res);
            return res;
        }, MyThreadPoolExecutor.threadPool);

        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3);
    }
}
