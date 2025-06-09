package com.khc.ddd.ch05;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.khc.ddd.Commons;
import com.khc.ddd.Members;
import com.khc.ddd.Orders;
import com.khc.ddd.Products;
import com.khc.ddd.app.member.domain.Member;
import com.khc.ddd.app.order.dao.OrderSummaryDao;
import com.khc.ddd.app.order.dao.OrderSummarySpecs;
import com.khc.ddd.app.order.domain.Order;
import com.khc.ddd.app.order.domain.OrderRepository;
import com.khc.ddd.app.order.domain.value.OrderNo;
import com.khc.ddd.app.order.dto.OrderSummary;
import com.khc.ddd.app.order.dto.OrderView;
import com.khc.ddd.app.product.domain.Product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SpecTest extends Commons {

	@Autowired
	private OrderSummaryDao orderSummaryDao;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	public void 주문자식별자로_OrderSummary_검색() {

		Member member = Members.getBasicMember();
		memberRepository.save(member);

		Product product = Products.getBasicProduct();
		productRepository.save(product);

		Order order = Orders.basicOrder(member, product);
		orderRepository.save(order);

		String orderer = order.getOrderer().getMemberId().getId();

		entityManager.flush();
		entityManager.clear();

		List<OrderSummary> orderSummaries = orderSummaryDao.findAll(OrderSummarySpecs.ordererId(orderer));

		assertThat(orderSummaries).isNotNull();
		assertThat(orderSummaries.size()).isEqualTo(1);
		assertThat(orderSummaries.get(0).getOrdererId()).isEqualTo(order.getOrderer().getMemberId().getId());
	}

	@Test
	public void 주문자식별자로_OrderSummary_검색_Then_List() {
		Member member = Members.getBasicMember();
		memberRepository.save(member);

		Product product = Products.getBasicProduct();
		productRepository.save(product);

		Order order = Orders.basicOrder(member, product);
		orderRepository.save(order);

		entityManager.flush();
		entityManager.clear();

		String orderer = order.getOrderer().getMemberId().getId();

		Pageable pageable = PageRequest.of(0, 10);

		Page<OrderSummary> orderSummaryPage = orderSummaryDao.findAll(OrderSummarySpecs.ordererId(orderer), pageable);

		orderSummaryPage.get().toList().get(0).setTotalAmounts(123414214);

		entityManager.flush();
		entityManager.clear();

		assertThat(orderSummaryPage).isNotNull();
	}

	@Test
	public void 주문자가_주문한_주문항목_중_첫번째_항목에_대한_목록조회() {
		Member member = Members.getBasicMember();
		memberRepository.save(member);

		Product product = Products.getBasicProduct();
		productRepository.save(product);

		Order order = Orders.basicOrder(member, product);
		orderRepository.save(order);

		String orderer = order.getOrderer().getMemberId().getId();

		List<OrderView> orderViews = orderSummaryDao.findOrderView(orderer);

		System.out.println(orderViews);

		assertThat(orderViews).isNotNull();

	}
}
