package com.khc.ddd.app.order.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.khc.ddd.app.order.dto.OrderSummary;
import com.khc.ddd.app.order.dto.OrderView;

public interface OrderSummaryDao extends Repository<OrderSummary, String> {

	List<OrderSummary> findAll(Specification<OrderSummary> spec);

	List<OrderSummary> findAll(Specification<OrderSummary> spec, Sort sort);

	// Page<OrderSummary> findByOrdererId(String ordererId, Pageable pageable);
	// findBy 프로퍼티 방식에서 Pageable을 사용하고 반환타입을 Page로 받는 전형적인 형태에서는 count 쿼리도 함께 발생한다.

	// List<OrderSummary> findByOrdererId(String ordererId, Pageable pageable);
	// findBy 프로퍼티 방식은 Pageable을 사용하더라도 반환타입이 List 라면 count 쿼리가 발생하지 않는다.

	// List<OrderSummary> findAll(Specification<OrderSummary> spec, Pageable pageable);
	// Spec을 사용하는 경우에도 Pageable을 사용할 수 있는데, 이 땐 List 로 반환받거나 Page로 반환받더라도 반드시 count 쿼리가 발생한다.

	Page<OrderSummary> findAll(Specification<OrderSummary> spec, Pageable pageable);

	@Query("""
		select new com.khc.ddd.app.order.dto.OrderView(
			o.number, o.state, m.name, m.memberId.id, p.name
		)
		from orders o 
			join o.orderLines ol
			join Member m on o.orderer.memberId = m.memberId
			join Product p on ol.productId = p.productId
		where o.orderer.memberId.id = :ordererId 
		and index(ol) = 0
		order by o.number.number desc
	""")
	List<OrderView> findOrderView(String ordererId);
	// 이렇게 하면 대신 엔티티가 아니라 바로 DTO로 매핑하기 때문에 엔티티의 각종 연관관계에 대한 고민을 떨쳐낼 순 있지만
	// Spec 을 사용할 수 없다.
}
