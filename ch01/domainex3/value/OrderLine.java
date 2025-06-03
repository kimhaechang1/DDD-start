package com.khc.ddd.ch01.domainex3.value;

import java.util.Objects;

import com.khc.ddd.ch01.domainex3.entity.Product;

public class OrderLine {

	private Product product;
	private int quantity;
	private Money price;
	private Money amounts;

	public OrderLine(Product product, int price, int quantity) {
		this.product = product;
		this.price = new Money(price);
		this.quantity = quantity;
		this.amounts = calculateAmounts();
	}

	private Money calculateAmounts() {
		return price.multiply(quantity);
	}

	public Money getAmounts() {
		return amounts;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		OrderLine orderLine = (OrderLine)o;
		return quantity == orderLine.quantity && Objects.equals(product, orderLine.product)
			&& Objects.equals(price, orderLine.price) && Objects.equals(getAmounts(),
			orderLine.getAmounts());
	}

	@Override
	public int hashCode() {
		return Objects.hash(product, quantity, price, getAmounts());
	}
}
