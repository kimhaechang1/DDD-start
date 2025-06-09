package com.khc.ddd;

import com.khc.ddd.app.product.domain.Product;
import com.khc.ddd.app.product.domain.ProductRepository;
import com.khc.ddd.app.product.domain.value.ProductId;

public class Products {

	public static Product getBasicProduct() {
		return new Product(ProductRepository.nextId(), "제품명");
	}
}
