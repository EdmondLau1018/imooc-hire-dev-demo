package com.imooc.exceptions;

import com.imooc.grace.result.ResponseStatusEnum;

/**
 * 封装异常，不直接在代码中抛出异常
 * 通过 displayException 方法将异常抛出 返回到前端
 */
public class GraceException {

    public static void displayException(ResponseStatusEnum responseStatusEnum){
        throw new MyCustomException(responseStatusEnum);
    }
}
