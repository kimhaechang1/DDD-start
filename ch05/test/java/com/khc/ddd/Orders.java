package com.khc.ddd;

import java.util.Arrays;
import java.util.List;

import com.khc.ddd.app.member.domain.Member;
import com.khc.ddd.app.member.domain.value.MemberId;
import com.khc.ddd.app.order.domain.Order;
import com.khc.ddd.app.order.domain.OrderRepository;
import com.khc.ddd.app.order.domain.OrderState;
import com.khc.ddd.app.order.domain.value.Address;
import com.khc.ddd.app.order.domain.value.Money;
import com.khc.ddd.app.order.domain.value.OrderLine;
import com.khc.ddd.app.order.domain.value.OrderNo;
import com.khc.ddd.app.order.domain.value.Orderer;
import com.khc.ddd.app.order.domain.value.Receiver;
import com.khc.ddd.app.order.domain.value.ShippingInfo;
import com.khc.ddd.app.product.domain.Product;
import com.khc.ddd.app.product.domain.value.ProductId;

public class Orders {

	public static Order basicOrder(final Member member, final Product ...products) {
		OrderNo id = OrderRepository.nextOrderNo();
		Orderer orderer = new Orderer(member.getMemberId(), member.getName());
		ShippingInfo shippingInfo = new ShippingInfo(
			new Address("대구광역시", "북구 동천로", "12345"),
			new Receiver("김회창", "010-1234-5678")
		);
		List<OrderLine> orderLines = Arrays.stream(products).map(p -> new OrderLine(p.getProductId(), 1000, 1)).toList();
		return new Order(id, orderer, orderLines, shippingInfo, OrderState.PREPARING);
	}
}
