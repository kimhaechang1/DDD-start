package com.khc.ddd.app.order.domain.value;

import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class Money {

	private int value;

	public Money(int value) {
		this.value = value;
	}

	protected Money() {

	}

	public Money multiply(int multiplier) {
		return new Money(value * multiplier);
	}

	public Money add(Money money) {
		return new Money(value + money.value);
	}

	public int getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Money money = (Money)o;
		return getValue() == money.getValue();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getValue());
	}
}