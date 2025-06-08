package com.khc.ddd.ch04;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.khc.ddd.ch04.member.domain.value.MemberId;
import com.khc.ddd.ch04.order.domain.entity.Order;
import com.khc.ddd.ch04.order.domain.value.OrderNo;
import com.khc.ddd.ch04.order.domain.value.Orderer;
import com.khc.ddd.ch04.order.domain.springdatajpa.OrderRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SpringDataJpaOrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Test
	public void SpringDataJpa를_활용한_레포지토리_테스트() {
		OrderNo id = OrderNo.of("반갑습니다.");
		MemberId memberId = MemberId.of("<UNK>");
		Orderer orderer = Orderer.of(memberId);
		Order order = new Order(id, orderer);
		orderRepository.save(order);

		Optional<Order> findOrder = orderRepository.findById(id);
		assertThat(findOrder).isPresent();
		assertThat(findOrder.get().getId().getNumber()).isEqualTo(order.getId().getNumber());
	}
}
