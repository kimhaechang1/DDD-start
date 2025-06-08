package com.khc.ddd.ch04.order.domain.value;

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

	public String getNumber() {
		return number;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		OrderNo orderNo = (OrderNo)o;
		return Objects.equals(getNumber(), orderNo.getNumber());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getNumber());
	}

	public static OrderNo of(String number) {
		return new OrderNo(number);
	}
}
