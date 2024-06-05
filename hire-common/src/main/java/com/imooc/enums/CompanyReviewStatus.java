package com.imooc.enums;

/**
 * @Desc: 企业 审核状态 枚举
 */
public enum CompanyReviewStatus {
    /**
     * 审核状态
     0：未发起审核认证(未进入审核流程)
     1：审核认证通过
     2：审核认证失败
     3：审核中（等待审核）
     */

    NOTHING(0, "未发起审核认证"),
    SUCCESSFUL(1, "审核认证通过"),
    FAILED(2, "审核认证失败"),
    REVIEW_ING(3, "审核中");

    public final Integer type;
    public final String value;

    CompanyReviewStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
