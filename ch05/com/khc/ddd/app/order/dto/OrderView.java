package com.khc.ddd.app.order.dto;

import com.khc.ddd.app.order.domain.OrderState;
import com.khc.ddd.app.order.domain.value.OrderNo;

public class OrderView {

	private final String number;

	@Override
	public String toString() {
		return "OrderView{" +
			"number='" + number + '\'' +
			", state=" + state +
			", memberName='" + memberName + '\'' +
			", memberId='" + memberId + '\'' +
			", productName='" + productName + '\'' +
			'}';
	}

	private final OrderState state;
	private final String memberName;
	private final String memberId;
	private final String productName;

	public OrderView(OrderNo number, OrderState state, String memberName, String memberId, String productName) {
		this.number = number.getNumber();
		this.state = state;
		this.memberName = memberName;
		this.memberId = memberId;
		this.productName = productName;
	}

	public String getNumber() {
		return number;
	}

	public OrderState getState() {
		return state;
	}

	public String getMemberName() {
		return memberName;
	}

	public String getMemberId() {
		return memberId;
	}

	public String getProductName() {
		return productName;
	}
}
