package com.imooc.test;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {


    @Test
    public void testSemaphore() throws InterruptedException {

        //  初始化公共资源 ，定义公共资源数为三个
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("当前线程：" + Thread.currentThread().getName() + "正在占用资源...");
                    Thread.sleep(3500);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "测试线程：" + (i + 1)).start();
        }

        Thread.sleep(10000000);
    }

}
