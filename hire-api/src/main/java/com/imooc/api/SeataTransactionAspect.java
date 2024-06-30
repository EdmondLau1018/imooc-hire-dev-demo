package com.imooc.api;

import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@Aspect
public class SeataTransactionAspect {

    /**
     * 通过切面开启全局事务，在service开始执行前开启
     *
     * @param joinPoint
     * @throws TransactionException
     */
    @Before("execution(* com.imooc.service.impl..*.*(..))")
    public void beginGlobalTransaction(JoinPoint joinPoint) throws TransactionException {

//        //  从全局事务上下文获取全局事务处理对象
//        GlobalTransaction globalTransaction = GlobalTransactionContext.getCurrentOrCreate();
//        //  开启全局事务
//        globalTransaction.begin();
    }

    /**
     * 切面实现：在当前服务中抛出错误的时候
     * 根据分布式事务 xid 获取当前事务 并进行手动回滚
     * @param throwable
     * @throws TransactionException
     */
    @AfterThrowing(throwing = "throwable"
            , pointcut = "execution(* com.imooc.service.impl..*.*(..))")
    public void seataRollBack(Throwable throwable) throws TransactionException {

        //  日志输出错误信息
        log.error("service 执行捕获到异常：{}", throwable.getMessage());

        //  通过 当前线程当前事务的 XID
        String xid = RootContext.getXID();
        //  判断当前获取的 xid 是否为空 （如果是空 则表示当前 出错的service中不含有分布式事务）
        if (StringUtils.isNotBlank(xid)) {

            //  通过事务上下文获取当前事务
            GlobalTransaction globalTransaction = GlobalTransactionContext.reload(xid);
            //  手动回滚当前事务
            globalTransaction.rollback();
        }
    }
}
