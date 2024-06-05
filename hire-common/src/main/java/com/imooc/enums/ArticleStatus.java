package com.imooc.enums;

/**
 * @Desc: 文章审核状态 枚举
 *        文章状态：
 *          0：关闭，待发布
 *          1：正常，可查阅，
 *          2：删除，无法查看
 */
public enum ArticleStatus {
    CLOSE(0, "关闭，待发布"),
    OPEN(1, "正常，可查阅"),
    DELETE(2, "删除，无法查看");

    public final Integer type;
    public final String value;

    ArticleStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

}
