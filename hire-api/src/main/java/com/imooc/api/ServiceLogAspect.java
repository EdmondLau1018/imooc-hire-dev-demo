package com.imooc.api;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@Aspect
public class ServiceLogAspect {

    /**
     * 环绕通知 获取 service 包下的所有 方法执行时间进行时间统计
     *
     * @param joinPoint
     * @return
     */
    @Around("execution(* com.imooc.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("task");
        //  执行当前方法
        Object proceed = joinPoint.proceed();
        //  获取执行的方法信息
        String point = joinPoint.getTarget().getClass().getName()
                + "." +
                joinPoint.getSignature().getName();
        stopWatch.stop();

        //  获取方法执行的时间和汇总信息
        log.info("当前执行的方法为： {}", point);
        log.info("执行方法的时间：{} 毫秒",stopWatch.getTotalTimeMillis());
        //  以表格形式输出信息
        log.info(stopWatch.prettyPrint());
        //  输出执行时间简单汇总信息
        log.info(stopWatch.shortSummary());

        return proceed;
    }
}
