package com.khc.ddd.app.order.domain;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.khc.ddd.app.order.domain.value.OrderNo;

public interface OrderRepository extends Repository<Order, OrderNo> {

	Optional<Order> findById(OrderNo orderNo);
	void save(Order order);

	public static OrderNo nextOrderNo() {
		return new OrderNo("order_"+System.currentTimeMillis());
	}
}
