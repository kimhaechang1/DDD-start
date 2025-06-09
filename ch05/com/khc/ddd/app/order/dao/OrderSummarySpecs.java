package com.khc.ddd.app.order.dao;

import java.time.LocalDateTime;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.khc.ddd.app.order.dto.OrderSummary;
import com.khc.ddd.app.order.dto.OrderSummary_;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class OrderSummarySpecs {

	public static Specification<OrderSummary> ordererId(String ordererId) {
		return (Root<OrderSummary> root,CriteriaQuery<?> query, CriteriaBuilder cb) ->
			cb.equal(root.get(OrderSummary_.ordererId), ordererId);
	}

	public static Specification<OrderSummary> orderDateBetween(LocalDateTime from, LocalDateTime to) {
		return (Root<OrderSummary> root,CriteriaQuery<?> query, CriteriaBuilder cb) ->
			cb.between(root.get(OrderSummary_.orderDate), from, to);
	}
}
