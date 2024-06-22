package com.imooc.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Sharn
 * @since 2024-06-22
 */
public class MqLocalMsgRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 目标交换机
     */
    private String targetExchange;

    /**
     * 消息路由信息
     */
    private String routingKey;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetExchange() {
        return targetExchange;
    }

    public void setTargetExchange(String targetExchange) {
        this.targetExchange = targetExchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "MqLocalMsgRecord{" +
        "id=" + id +
        ", targetExchange=" + targetExchange +
        ", routingKey=" + routingKey +
        ", msgContent=" + msgContent +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
