package com.imooc.enums;

/**
 * @Desc: 消息接受者类型
 */
public enum MsgReveiverTypeEnum {

    HR(1, "HR"),
    CANDIDATE(2, "求职者");

    public final Integer type;
    public final String value;

    MsgReveiverTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
