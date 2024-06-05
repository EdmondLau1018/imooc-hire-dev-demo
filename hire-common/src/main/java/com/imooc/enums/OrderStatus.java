package com.imooc.enums;

/**
 * @Description: 性别 枚举
 */
public enum OrderStatus {

	/*10：待付款
	20：已付款，待发货
	30：已发货，待收货（7天自动确认）
	40：交易成功（此时可以评价）
	50：交易关闭（待付款时，用户取消 或 长时间未付款，系统识别后自动关闭）
	退货/退货，此分支流程不支持，所以不加入*/

	WAIT_PAY(10, "待付款"),
//	WAIT_DELIVER(20, "已付款，待发货"),
//	WAIT_RECEIVE(30, "已发货，待收货"),
	SUCCESS(40, "交易成功"),
	CLOSE(50, "交易关闭");

	public final Integer type;
	public final String value;

	OrderStatus(Integer type, String value){
		this.type = type;
		this.value = value;
	}

}
