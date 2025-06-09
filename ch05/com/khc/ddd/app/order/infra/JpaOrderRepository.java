package com.khc.ddd.app.order.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.khc.ddd.app.order.domain.Order;
import com.khc.ddd.app.order.domain.OrderPureRepository;
import com.khc.ddd.app.order.domain.OrderRepository;
import com.khc.ddd.app.order.domain.value.OrderNo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class JpaOrderRepository implements OrderPureRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Order findById(OrderNo orderNo) {
		return entityManager.find(Order.class, orderNo);
	}

	@Override
	public void save(Order order) {
		entityManager.persist(order);
	}
}
