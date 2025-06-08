package com.khc.ddd.ch04;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.khc.ddd.ch04.member.domain.value.MemberId;
import com.khc.ddd.ch04.order.domain.entity.Order;
import com.khc.ddd.ch04.order.domain.value.OrderNo;
import com.khc.ddd.ch04.order.domain.value.Orderer;
import com.khc.ddd.ch04.order.infra.JpaOrderRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaOrderRepository.class)
public class JpaOrderRepositoryTest {

	@Autowired
	private JpaOrderRepository repository;

	@Test
	public void 엔티티메니져를_활용한_레포지토리_테스트() {
		OrderNo id = OrderNo.of("반갑습니다.");
		MemberId memberId = MemberId.of("<UNK>");
		Orderer orderer = Orderer.of(memberId);
		Order order = new Order(id, orderer);
		repository.save(order);

		Order findOrder = repository.findById(id);
		assertThat(findOrder).isNotNull();
		assertThat(findOrder.getId().getNumber()).isEqualTo(order.getId().getNumber());
	}
}
