package com.khc.ddd.app.product.domain;

import java.util.UUID;

import org.springframework.data.repository.Repository;

import com.khc.ddd.app.product.domain.value.ProductId;

public interface ProductRepository extends Repository<Product, ProductId> {

	Product findById(ProductId id);
	void save(Product product);

	public static ProductId nextId() {
		return new ProductId("product_" + UUID.randomUUID());
	}
}
