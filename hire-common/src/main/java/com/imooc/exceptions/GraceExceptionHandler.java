package com.imooc.exceptions;

import com.imooc.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常拦截处理
 * 可以针对异常的类型进行捕获，然后返回json信息到前端
 */
@ControllerAdvice  //  Springboot 拦截器注解
public class GraceExceptionHandler {

    /**
     * 通过 AOP 的方式 拦截和处理请求
     * 将异常的返回结果包装成一个 json 字符串
     * @return
     */
    @ExceptionHandler(MyCustomException.class)  //  处理的请求为 MyCustomException
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e){
        e.printStackTrace();
        //  通过自定义异常获取 枚举中的信息
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }
}
