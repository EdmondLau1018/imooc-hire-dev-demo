package com.imooc.test.threads;

import java.util.UUID;
import java.util.concurrent.Callable;

public class CallableClass_02 implements Callable<Object> {

    @Override
    public Object call() throws Exception {

        String result = UUID.randomUUID().toString();

        System.out.println("当前线程的编号为： " + Thread.currentThread().getId() + "\n"
                + "当前线程创建的类为：" + "CallableClass_02"+ "\n"
                + "这个线程会返回一个 结果 在这里定义成了 Object 类型：" + result);

        return result;
    }
}
