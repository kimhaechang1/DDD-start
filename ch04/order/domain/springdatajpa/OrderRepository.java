package com.khc.ddd.ch04.order.domain.springdatajpa;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.khc.ddd.ch04.order.domain.entity.Order;
import com.khc.ddd.ch04.order.domain.value.OrderNo;

public interface OrderRepository extends Repository<Order, OrderNo> {
	Optional<Order> findById(OrderNo id);
	void save(Order order);
}
