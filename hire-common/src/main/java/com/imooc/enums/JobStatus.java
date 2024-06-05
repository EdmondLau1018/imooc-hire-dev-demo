package com.imooc.enums;

/**
 * @Desc: 职位状态
 */
public enum JobStatus {
    OPEN(1, "招聘中"),
    CLOSE(2, "已关闭"),
    DELETE(3, "违规删除");

    public final Integer type;
    public final String value;

    JobStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
