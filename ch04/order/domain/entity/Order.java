package com.khc.ddd.ch04.order.domain.entity;

import com.khc.ddd.ch04.order.domain.value.OrderNo;
import com.khc.ddd.ch04.order.domain.value.Orderer;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@EmbeddedId
	private OrderNo id;

	@Embedded
	private Orderer orderer;

	protected Order() {

	}

	public Order(OrderNo id, Orderer orderer) {
		this.id = id;
		this.orderer = orderer;
	}

	public OrderNo getId() {
		return id;
	}
}
