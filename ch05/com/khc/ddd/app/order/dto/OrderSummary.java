package com.khc.ddd.app.order.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Immutable
@Subselect("""
	select 
	    o.order_number as number,
	    o.orderer_id,
	    o.orderer_name,
	    o.total_amounts,
	    o.receiver_name,
	    o.state,
	    o.order_date,
	    p.product_id,
	    p.name as product_name
	from orders o 
	    join order_line ol on o.order_number = ol.order_number
		join product p on ol.product_id = p.product_id
	where
	    ol.line_idx = 0
""")
// subselect와 세트로 immutable 을 사용하면 서브쿼리로 하나의 뷰에 대한 엔티티를 만들 수 있고 ReadOnly 형태로 동작하도록 보장할 수 있다.
// 또한 엔티티성이 그대로 보장되기에 Criteria API 나 JPQL 그리고 Specification<T> 를 사용할 수 있다.
// 다만 서브쿼리로 동작하는 것 이기 때문에, 성능에 유의해야 한다.
@Synchronize({"orders", "product", "order_line"})
// 만약 하나의 영속성 컨텍스트 내에서 조회 전 위에 해당하는 테이블에 관한 변경이 발생할 때에도 싱크가 맞아떨어지도록 도와주는 어노테이션이다.
// 이 어노테이션이 없다면 flush 가 발생하지 않기에, DB에 insert 쿼리나 update 쿼리가 발생하기 전 상태에서 조회를 하게 된다.
public class OrderSummary {

	@Id
	private String number;

	@Column(name = "orderer_id")
	private String ordererId;
	@Column(name = "orderer_name")
	private String ordererName;
	@Column(name = "total_amounts")
	private int totalAmounts;
	@Column(name = "receiver_name")
	private String receiverName;
	private String state;
	@Column(name = "order_date")
	private LocalDateTime orderDate;
	@Column(name = "product_id")
	private String productId;
	@Column(name = "product_name")
	private String productName;

	protected OrderSummary() {
	}

	public String getNumber() {
		return number;
	}

	public String getOrdererId() {
		return ordererId;
	}

	public String getOrdererName() {
		return ordererName;
	}

	public int getTotalAmounts() {
		return totalAmounts;
	}

	public void setTotalAmounts(int totalAmounts) {
		this.totalAmounts = totalAmounts;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public String getState() {
		return state;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public String getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}
}
