package com.imooc.enums;

/**
 * @Desc: 处理举报状态 枚举
 * 处理状态：0：待处理，1：已处理，2：忽略、无需处理
 */
public enum DealStatus {
    WAITING(0, "待处理"),
    DONE(1, "已处理"),
    IGNORE(2, "忽略、无需处理");

    public final Integer type;
    public final String value;

    DealStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
