package com.khc.ddd.app.order.domain;

import com.khc.ddd.app.order.domain.value.OrderNo;

public interface OrderPureRepository {

	Order findById(OrderNo number);
	void save(Order order);
}
