package com.imooc.enums;

/**
 * @Desc: 显示用户名字 枚举
 */
public enum ShowWhichName {
    realname(1, "真实姓名"),
    nickname(2, "昵称");

    public final Integer type;
    public final String value;

    ShowWhichName(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
