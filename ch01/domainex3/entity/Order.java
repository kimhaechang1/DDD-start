package com.khc.ddd.ch01.domainex3.entity;

import java.util.List;
import java.util.Objects;

import com.khc.ddd.ch01.domainex3.value.Money;
import com.khc.ddd.ch01.domainex3.value.OrderLine;
import com.khc.ddd.ch01.domainex3.value.ShippingInfo;

public class Order {

	/*
	* 엔티티
	* 객체의 종류중에 엔티티의 가장 중요한 특징은 바로 식별자이다.
	* 고유한 식별자를 가지고 같은 종류의 엔티티들 사이에서 동일함을 구별한다.
	* */

	/*
	* 벨류(Value)
	* 벨류는 객체의 종류중에 식별자가 없으며, 모든 필드가 하나의 값을 이루는 객체를 말한다.
	* 모든 필드가 하나의 값 객체를 완성하는 부분이기에, 필드값을 기준으로 동일함을 구별한다.
	* 또한 원시타입의 경우, 같은 원시타입에도 불구하고 다른 의미를 가질 수 있기 때문에 필드의 개수와 상관없이 값을 표현하여야 한다.
	* */

	/*
	* Order는 주문이고, 요구사항에 따라 일반적으로 서로다른 두 주문은 주문번호라는 개념을 통해 구분된다.
	* 따라서 OrderNo 필드를 통해 구분되어야 하기에 해당 필드로 hashCode() 와 equals()를 오버라이딩 해야한다.
	* Product도 마찬가지이다. 같은 상품이름이어도 서로 다른 상품이 될 수 있다. 별도의 식별자가 필요할 것이다.
	*
	* 하지만 ShippedInfo를 보자. 이것은 어떤 고유한 식별자를 가지는것이 아니다. 그냥 주소입력값 필드중에 하나라도 다르면 다른 ShippedInfo라고 볼 수 있다.
	* 따라서 ShippedInfo는 벨류가 될 수 있다.
	* 또한 내부에 receiverPhoneNumber 와 receiverName은 어찌보면 배송지 정보 내에 또하나의 벨류인 받는사람에 해당할 수 있다.
	* 그리고 나머지 shippingAddress1 ~ shippingZipcode는 어찌보면 배송지 정보 내에 또하나의 밸류인 주소에 해당할 수 있다.
	* 이처럼 벨류 내부에 또다른 벨류로 세팅 할 수 있게 된다.
	*
	* 마지막으로 총 금액을 계산할 때에도 int 타입을 쓰는것 보다는, 이건 돈 타입입니다 라고 표현하기 위해 Money라고 하는 벨류를 사용할 수 있다.
	* 특히나 벨류 오브젝트들은 불변임을 유지하는것이 좋다. 즉, setter를 왠만하면 만들지 않는것이 좋다. 만약 필드의 변화를 주어야 한다면, 새로운 벨류오브젝트를 반환하여 참조 투명성을 지켜야한다.
	* 예를들어 Money 객체를 만들고 OrderLine을 하나 만든다고 가정해보자.
	*
	* Money price = new Money(2000);
	* OrderLine line = new OrderLine(product, price, 2)
	* print(line); -> [price=1000, quantity=2, amounts=2000]
	* price.setValue(2000);
	* print(line); -> [price=2000, quantity=2, amounts=2000] : 오류발생!
	*
	* 그렇게 불변객체화 시키다 보면 중요한 공통점으로 setter 메소드의 경우 모두 private으로 작성되어지는 모습을 볼 수 있다.
	* */
	private String orderNumber;
	private List<OrderLine> orderLines;
	private ShippingInfo shippingInfo;
	private Money totalAmounts;
	private OrderState state;
	private Orderer orderer;

	public Order(List<OrderLine> orderLines, ShippingInfo shippingInfo, OrderState state, Orderer orderer) {
		// 이전에 여기서 verity를 진행하였는데, 사실 생성자는 주문을 생성하는 역할이 가장 중요하다.
		// orderLines에 대한 검증메소드는 사실상에 OrderLine을 세팅하는 메소드에서 가져야한다.
		setOrderLines(orderLines);
		setShippingInfo(shippingInfo);
		// 주문자가 누락되는 경우가있다. 특히 주문자는 별도의 객체이기에 null 인지 검사도 해야한다.
		// 그런 별도의 도메인 규칙이 작동하는 setter 메소드는 private으로 만들자.
		// 이런 것들은 모두 Order가 생성될때 필수 요소들이기 때문에 필수 요소들은 반드시 생성자를 통해 채워주도록 하자.
		this.state = state;
		setOrderer(orderer);
	}

	private void setOrderer(Orderer orderer) {
		if (orderer == null) {
			throw new IllegalArgumentException("no orderer");
		}
		this.orderer = orderer;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Order order = (Order)o;
		return Objects.equals(orderNumber, order.orderNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(orderNumber);
	}

	private void setShippingInfo(ShippingInfo shippingInfo) {
		if (shippingInfo == null) {
			throw new IllegalArgumentException("no ShippingInfo");
		}
		this.shippingInfo = shippingInfo;
	}

	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<OrderLine> orderLines) {
		verifyAtLeastOneOrMoreOrderLines(orderLines);
		this.orderLines = orderLines;
		calculateTotalAmount();
	}

	private void verifyAtLeastOneOrMoreOrderLines(List<OrderLine> orderLines) {
		// '최소 한 종류 이상의 상품을 주문해야 함' 라는 도메인 규칙을 지키는지 검증함
		if (orderLines == null || orderLines.isEmpty()) {
			throw new IllegalArgumentException("no orderLines");
		}
	}

	private void calculateTotalAmount() {
		// 각 상품별로 개수 * 상품 가격을 한 주문항목의 총 가격이 있고
		// 해당 총 가격을 합하면 주문의 총 가격이 된다.
		this.totalAmounts = new Money (orderLines.stream()
			.mapToInt(x -> x.getAmounts().getValue())
			.sum()
		);
	}

	// 배송지를 변경할 수 있어야 한다.
	public void changeShippingInfo(ShippingInfo shippingInfo) {

	}

	// 주문을 취소할 수 있어야 한다.
	public void cancel() {

	}

	// 결제 완료로 넘어갈 수 있어야 한다.
	// 결제가 완료되면 상품 대기중이어야 한다.
	public void completePayment() {

	}

	// 상품 준비가 완료되면, 출고 상태로 변경하여야 한다.
	public void changeShipped() {

	}

	private void verifyNotYetShipped() {
		if (state != OrderState.PAYMENT_WAITING && state != OrderState.PREPARING) {
			throw new IllegalArgumentException("already shipped");
		}
	}

	public enum OrderState {

		// 일단 배송중과 배송완료를 기본전제로 들어간다.
		// 그리고 출고 이전에는 배송지 변경과 주문취소가 가능하다 라는 제약이 있다.
		// 그러면 출고 이전의 상태인 결제 중과 상품 준비중 그리고 주문취소라는 상태를 포함할 수 있다.

		// 출고 이전에는 배송지 변경과 주문취소가 가능하다 라는 제약을 위해서 verifyNotYetShipped라는 메소드를 구현하자.
		// 이전에는 isShippingChangeable이었지만, 주문이라는 도메인을 깊게 이해해보니 같은 조건으로 제약을 받는 상태들이 존재함에 따라 명칭을 변경한다.
		PAYMENT_WAITING,
		PREPARING,
		SHIPPED, DELIVERED, DELIVERY_COMPLETED, CANCELED;
	}
}
