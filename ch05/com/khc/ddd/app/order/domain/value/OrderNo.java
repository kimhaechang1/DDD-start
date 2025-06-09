package com.khc.ddd.app.order.domain.value;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrderNo implements Serializable {

	@Column(name = "order_number")
	private String number;

	protected OrderNo() {

	}

	public OrderNo(String number) {
		this.number = number;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		OrderNo orderId1 = (OrderNo)o;
		return Objects.equals(number, orderId1.number);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(number);
	}

	public static OrderNo of(String number) {
		return new OrderNo(number);
	}

	public String getNumber() {
		return number;
	}
}
