package com.khc.ddd.app.order.domain.value;

import com.khc.ddd.app.product.domain.value.ProductId;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class OrderLine {

	@Embedded
	private ProductId productId;

	@Convert(converter = MoneyConverter.class)
	@Column(name = "price")
	private Money price;

	@Column(name = "quantity")
	private int quantity;

	@Convert(converter = MoneyConverter.class)
	@Column(name = "amounts")
	private Money amounts;

	protected OrderLine() {
	}

	public OrderLine(ProductId productId, int price, int quantity) {
		this.productId = productId;
		this.price = new Money(price);
		this.quantity = quantity;
		this.amounts = calculateAmounts();
	}

	private Money calculateAmounts() {
		return price.multiply(quantity);
	}

	public ProductId getProductId() {
		return productId;
	}

	public Money getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public Money getAmounts() {
		return amounts;
	}
}
