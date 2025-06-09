package com.khc.ddd.app.order.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.khc.ddd.app.order.domain.value.Money;
import com.khc.ddd.app.order.domain.value.MoneyConverter;
import com.khc.ddd.app.order.domain.value.OrderLine;
import com.khc.ddd.app.order.domain.value.OrderNo;
import com.khc.ddd.app.order.domain.value.Orderer;
import com.khc.ddd.app.order.domain.value.ShippingInfo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;

@Entity(name = "orders")
public class Order {

	@EmbeddedId
	private OrderNo number;

	@Embedded
	private Orderer orderer;

	@ElementCollection
	@CollectionTable(name = "order_line", joinColumns = @JoinColumn(name = "order_number"))
	@OrderColumn(name = "line_idx")
	private List<OrderLine> orderLines;

	@Convert(converter = MoneyConverter.class)
	@Column(name = "total_amounts")
	private Money totalAmounts;

	@Embedded
	private ShippingInfo shippingInfo;

	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private OrderState state;

	@Column(name = "order_date")
	private LocalDateTime orderDate;

	protected Order() {

	}

	public Order(OrderNo number, Orderer orderer, List<OrderLine> orderLines, ShippingInfo shippingInfo, OrderState state) {
		setNumber(number);
		setOrderer(orderer);
		setOrderLines(orderLines);
		setShippingInfo(shippingInfo);
		this.state = state;
		this.orderDate = LocalDateTime.now();
	}

	public Orderer getOrderer() {
		return orderer;
	}

	public OrderNo getNumber() {
		return number;
	}

	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public Money getTotalAmounts() {
		return totalAmounts;
	}

	public ShippingInfo getShippingInfo() {
		return shippingInfo;
	}

	public OrderState getState() {
		return state;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	private void setNumber(OrderNo number) {
		if (Objects.isNull(number)) throw new IllegalArgumentException("no number");
		this.number = number;
	}

	private void setOrderer(Orderer orderer) {
		if (Objects.isNull(orderer)) throw new IllegalArgumentException("no orderer");
		this.orderer = orderer;
	}

	private void setOrderLines(List<OrderLine> orderLines) {
		verifyAtLeastOneOrMoreOrderLines(orderLines);
		// 적어도 하나이상의 주문항목을 가지고있는지
		this.orderLines = orderLines;
		calculateTotalAmounts();
	}

	private void verifyAtLeastOneOrMoreOrderLines(List<OrderLine> orderLines) {
		if (Objects.isNull(orderLines) || orderLines.isEmpty())
			throw new IllegalArgumentException("no order lines");
	}

	private void calculateTotalAmounts() {
		this.totalAmounts = new Money(
			this.orderLines.stream()
			.mapToInt(x -> x.getPrice().getValue())
			.sum()
		);
	}

	private void setShippingInfo(ShippingInfo shippingInfo) {
		this.shippingInfo = shippingInfo;
	}

	public void changeShippingInfo(ShippingInfo newShippingInfo) {
		verifyNotYetShipped();
		setShippingInfo(newShippingInfo);
	}

	public void cancel() {
		verifyNotYetShipped();
		this.state = OrderState.CANCELED;
	}

	private void verifyNotYetShipped() {
		if (!isNotYetShipped())
			throw new IllegalStateException("already shipped");
	}

	private boolean isNotYetShipped() {
		return this.state == OrderState.PAYMENT_WAITING || this.state == OrderState.PREPARING;
	}

	public void startShipping() {
		verifyShippableState();
		this.state = OrderState.SHIPPED;
	}

	private void verifyShippableState() {
		// 선적 가능한 상태 일려면, 적어도 결제 완료 후 상품 준비중인 상태거나 혹은 주문이 취소되지 않아야 한다.
		verifyNotYetShipped();
		verifyNotCanceled();
	}

	private void verifyNotCanceled() {
		if (state == OrderState.CANCELED) {
			// 무슨행동을 하던간에 이미 취소된 주문에 대하여 명령을 실행하는 경우에는 예외를 터트려야한다.
			throw new IllegalStateException("canceled");
		}
	}

}
