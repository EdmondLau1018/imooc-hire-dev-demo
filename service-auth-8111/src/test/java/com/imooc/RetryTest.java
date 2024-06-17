package com.imooc;

import com.imooc.api.retry.RetryComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RetryTest {

    @Autowired
    private RetryComponent retryComponent;

    @Test
    public void testRetry() {
        boolean b = retryComponent.sendSMSWithRetry();
        log.info("当前重试机制测试结果：{}", b);
    }

}
