package com.imooc.enums;

/**
 * @Desc: 面试邀约的状态
 *          1：等待候选人接受面试
 *          2：候选人已接受面试
 *          3：候选人已拒绝面试
 *          4：HR已取消面试
 *          5：面试通过
 */
public enum InterviewStatusEnum {
    WAITING(1, "等待候选人接受面试"),
    ACCEPT(2, "候选人已接受面试"),
    REFUSE(3, "候选人已拒绝面试"),
    CANCEL(4, "HR已取消面试"),
    SUCCESS(5, "面试通过");     // 此状态无用，平台的边界只在HR找到求职者来面试，至于面试过后的情况，是无所谓的，而且也是属于企业内部招聘管理的事宜了

    public final Integer type;
    public final String value;

    InterviewStatusEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
