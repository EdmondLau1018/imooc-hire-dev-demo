package com.imooc.api.retry;

import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.SMSUtilsRetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class RetryComponent {

    @Retryable(include = {
            //  重试机制捕获的异常 如果重试函数调用的方法中出现了这个异常则就触发重试
            IllegalArgumentException.class,
            ArrayIndexOutOfBoundsException.class
    },
            exclude = {
                    //  exclude 定义的 异常不会触发重试机制
                    NullPointerException.class
            },
            maxAttempts = 5,    //  最多重试 5 次
            backoff = @Backoff(
                    //  第一次 重试时间 1秒钟
                    delay = 1000L,
                    // 下一次时间间隔是上一次重试的二倍
                    multiplier = 2
            ))
    public boolean sendSMSWithRetry() {
        log.info("当前时间：{}", LocalDateTime.now());
        //  调用需要重试的方法
        return SMSUtilsRetry.sendSMSCode();
    }

    /**
     * 重试机制组件的兜底方法 ，
     * 达到了最大重试次数 或者 捕获到了没有指定的异常
     */
    @Recover
    public void recover(){
        GraceException.displayException(ResponseStatusEnum.SYSTEM_SMS_FALLBACK_ERROR);
    }
}
