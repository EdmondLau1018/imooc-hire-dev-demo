package com.imooc.exceptions;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一异常拦截处理
 * 可以针对异常的类型进行捕获，然后返回json信息到前端
 */
@ControllerAdvice  //  Springboot 拦截器注解
public class GraceExceptionHandler {

    /**
     * 通过 AOP 的方式 拦截和处理请求
     * 将异常的返回结果包装成一个 json 字符串
     *
     * @return
     */
    @ExceptionHandler(MyCustomException.class)  //  处理的请求为 MyCustomException
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e) {
        e.printStackTrace();
        //  通过自定义异常获取 枚举中的信息
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

    /**
     * 用于捕获参数校验的异常
     * 将参数校验异常封装成 json 字符串返回前端进行解析
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GraceJSONResult returnMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        //  获取参数校验异常对应的结果
        BindingResult result = e.getBindingResult();
        //  获取异常的状态和错误信息
        Map<String, String> map = getErrors(result);
        //  将信息封装成json对象 抛出给前端
        return GraceJSONResult.errorMap(map);
    }

    /**
     * 捕获 用户身份 JWT 校验时发生的异常
     * 通过 GraceJsonResult 返回
     * @param e
     * @return
     */
    @ExceptionHandler({
            SignatureException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            io.jsonwebtoken.security.SignatureException.class
    })
    @ResponseBody
    public GraceJSONResult resultSignatureException(SignatureException e){
        e.printStackTrace();
        return GraceJSONResult.exception(ResponseStatusEnum.JWT_SIGNATURE_ERROR);
    }

    /**
     * 通用：获取异常中的错误信息 返回一个 Map 对象
     *
     * @param result
     * @return
     */
    public Map<String, String> getErrors(BindingResult result) {
        HashMap<String, String> map = new HashMap<>();
        //  获取异常中所有的错误信息
        List<FieldError> fieldErrorList = result.getFieldErrors();
        for (FieldError fieldError : fieldErrorList) {
            //  获取异常的状态
            String field = fieldError.getField();
            //  获取异常的错误信息
            String message = fieldError.getDefaultMessage();
            map.put(field, message);
        }
        return map;
    }
}
