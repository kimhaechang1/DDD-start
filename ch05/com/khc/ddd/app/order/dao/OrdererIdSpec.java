package com.khc.ddd.app.order.dao;

import org.springframework.data.jpa.domain.Specification;

import com.khc.ddd.app.order.domain.value.Orderer;
import com.khc.ddd.app.order.dto.OrderSummary;
import com.khc.ddd.app.order.dto.OrderSummary_;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class OrdererIdSpec implements Specification<OrderSummary> {

	private String ordererId;

	public OrdererIdSpec(String ordererId) {
		this.ordererId = ordererId;
	}

	@Override
	public Predicate toPredicate(Root<OrderSummary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		return criteriaBuilder.equal(root.get(OrderSummary_.ordererId), ordererId);
	}
}
