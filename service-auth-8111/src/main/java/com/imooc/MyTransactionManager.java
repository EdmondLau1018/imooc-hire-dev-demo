package com.imooc;

import com.imooc.mq.InitResumeMQProducerHandler;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;

/**
 * 目的是 优化业务代码 在本地保存消息的事务提交之后
 * 自动发送消息到 MQ 让另外的微服务进行消费
 */
@Component
public class MyTransactionManager extends DataSourceTransactionManager {

    private final InitResumeMQProducerHandler resumeMQProducerHandler;

    public MyTransactionManager(DataSource dataSource, InitResumeMQProducerHandler resumeMQProducerHandler) {
        super(dataSource);
        this.resumeMQProducerHandler = resumeMQProducerHandler;
    }

    /**
     * 重写 doCommit 方法 在事务提交之后 查询本地消息表中 未发送的消息 发送到 MQ 中
     * @param status
     */
    @Override
    protected void doCommit(DefaultTransactionStatus status) {

        try {
            //  当前事务提交
            super.doCommit(status);
        } finally {
            // 发送本地所有未发送的消息
            resumeMQProducerHandler.sendAllLocalMsg();
        }
    }
}
