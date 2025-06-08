package com.khc.ddd.ch04.order.domain;

import com.khc.ddd.ch04.order.domain.entity.Order;
import com.khc.ddd.ch04.order.domain.value.OrderNo;

public interface OrderRepository {
	Order findById(OrderNo no);
	void save(Order order);
}
