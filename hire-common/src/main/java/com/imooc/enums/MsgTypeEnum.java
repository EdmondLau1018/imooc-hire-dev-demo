package com.imooc.enums;

/**
 * 
 * @Description: 发送消息的类型/动作 枚举
 */
public enum MsgTypeEnum {
	
	CONNECT_INIT(0, "第一次(或重连)初始化连接"),
	WORDS(1, "文字表情消息"),
	IMAGE(2, "图片"),
	VOICE(3, "语音"),
	VIDEO(4, "视频"),
	RESUME(5, "简历"),
	JOB_OFFER(6, "录用通知"),

	INVITE(7, "面试邀约"),
	MSG_INTERVIEW_CANCEL(71, "HR取消面试"),
	MSG_INTERVIEW_REFUSE(72, "候选人拒绝面试"),
	MSG_INTERVIEW_ACCEPT(73, "候选人接受面试"),
	MSG_LOADING(911, "消息长时间等待加载效果"),

	SIGNED(8, "消息签收"),
	KEEPALIVE(9, "客户端保持心跳"),
	heart(10, "拉取好友");
	
	public final Integer type;
	public final String content;
	
	MsgTypeEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
