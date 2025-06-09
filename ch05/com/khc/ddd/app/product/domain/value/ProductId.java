package com.khc.ddd.app.product.domain.value;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductId implements Serializable {

	@Column(name = "product_id")
	private String id;

	protected ProductId() {

	}

	public ProductId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ProductId productId = (ProductId)o;
		return Objects.equals(id, productId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	public String getId() {
		return id;
	}

	public static ProductId of(String id) {
		return new ProductId(id);
	}
}
