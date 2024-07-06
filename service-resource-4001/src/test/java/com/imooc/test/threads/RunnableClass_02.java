package com.imooc.test.threads;

public class RunnableClass_02 implements Runnable{
    @Override
    public void run() {
        System.out.println("当前线程的编号为： " + Thread.currentThread().getId()
                + "当前线程创建的类为：" + "RunnableClass_02");
    }
}
