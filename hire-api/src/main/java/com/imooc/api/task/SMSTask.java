package com.imooc.api.task;

import com.imooc.api.retry.RetryComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SMSTask {

    private final RetryComponent retryComponent;

    public SMSTask(RetryComponent retryComponent) {
        this.retryComponent = retryComponent;
    }

    /**
     * 异步发送短信的注解
     */
    @Async
    public void sendSMSAsync() {
        boolean result = retryComponent.sendSMSWithRetry();
        log.info("异步任务，最终的运行结果为：{}", result);
    }
}
