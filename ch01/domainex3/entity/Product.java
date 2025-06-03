package com.khc.ddd.ch01.domainex3.entity;

import java.util.Objects;

public class Product {

	private int id;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Product product = (Product)o;
		return id == product.id;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
