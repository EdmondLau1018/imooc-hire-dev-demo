package com.imooc.exceptions;

import com.imooc.grace.result.ResponseStatusEnum;

/**
 * 自定义异常处理 参数是 返回枚举 （ResponseEnum）
 */
public class MyCustomException extends RuntimeException{

    private ResponseStatusEnum responseStatusEnum;

    public MyCustomException(ResponseStatusEnum responseStatusEnum) {
        // 使用父类的构造方法 打印当前错误信息
        super("异常状态码为：" + responseStatusEnum.status()
                + "\n" +
                "异常信息为：" + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }

    public ResponseStatusEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }

    public void setResponseStatusEnum(ResponseStatusEnum responseStatusEnum) {
        this.responseStatusEnum = responseStatusEnum;
    }

}
