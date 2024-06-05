package com.imooc.api.interceptor;

import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SMSInterceptor extends BaseInfoProperties implements HandlerInterceptor {
    /**
     * 请求前置拦截 :访问 controller 之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //  获取当前用户的 真实 ip
        String userIp = IPUtil.getRequestIp(request);
        //  构建 key 判断当前 key 在 redis 中是否存在 （是否超过 60 秒）
        String redisKey = MOBILE_SMSCODE + ":" + userIp;
        boolean isExist = redis.keyIsExist(redisKey);
        if (isExist) {
            //  发送短信的频率小于 60 秒钟 可以进行拦截
            log.error("当前请求：/passoprt/getSMSCode 被拦截，原因：请求频率过高");
            //  通过 displayException 的方式 抛出异常（相当于在业务代码中直接调用方法 而不是抛出异常，减少异常对业务代码的侵入性）
            GraceException.displayException(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }

    /**
     * 请求拦截：访问controller之后，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 请求后置拦截：渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
