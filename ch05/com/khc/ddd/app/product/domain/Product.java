package com.khc.ddd.app.product.domain;

import java.util.Objects;

import com.khc.ddd.app.product.domain.value.ProductId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class Product {

	@EmbeddedId
	private ProductId productId;

	private String name;

	protected Product() {

	}

	public Product(ProductId productId, String name) {
		this.productId = productId;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ProductId getProductId() {
		return productId;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Product product = (Product)o;
		return Objects.equals(getProductId(), product.getProductId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getProductId());
	}
}
