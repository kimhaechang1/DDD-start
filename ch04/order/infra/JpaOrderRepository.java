package com.khc.ddd.ch04.order.infra;

import org.springframework.stereotype.Repository;

import com.khc.ddd.ch04.order.domain.entity.Order;
import com.khc.ddd.ch04.order.domain.value.OrderNo;
import com.khc.ddd.ch04.order.domain.OrderRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class JpaOrderRepository implements OrderRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Order findById(OrderNo no) {
		return em.find(Order.class, no);
	}

	@Override
	public void save(Order order) {
		em.persist(order);
	}
}
