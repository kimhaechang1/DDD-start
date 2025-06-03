## 에그리거트

에그리거트는 엔티티와 벨류를 하나로 묶은 군집이다.

에그리거트에는 루트 엔티티가 존재하며 내부의 다른 엔티티와 벨류를 관장한다.

루트 엔티티를 통해 다양한 도메인로직을 제공한다.

여기서 에그리거트가 되는 엔티티와 벨류들의 군집화 기준은 `비슷하거나 같은 생명주기를 갖는 것들을 뜻`한다.

주문은 주문자, 주문 항목, 배송지 등을 가지고 있다. 주문 항목은 주문없이 절대 생길 수 없다. 일반적으로 말이안된다.

주문은 생성되었는데 주문자가 없다거나 배송지가 없다거나 그리고 주문항목이 없으면 안된다. 이는 도메인 규칙을 통해 정해지기도 하고 일반적으로 말이안되기도 한다.

따라서 `주문` 이라는 에그리거트에는 `Order` 엔티티와 `OrderLine` 벨류, `Orderer` 벨류, `ShippingInfo` 벨류가 하나의 에그리거트로 묶인다.

### 주문 에그리거트 구성: 주문자와 회원

여기서 `주문자` 헷갈릴 수 있다. 일반적으로 회원을 관리하는 에그리거트는 별도로 사용할 것이다.

회원 도메인에서는 회원 탈퇴, 회원 가입과 같은 도메인 로직을 포함한다고 해보자.

여기서 말하는 `주문자`는 도메인 모델로서의 회원이라고 생각하여 주문 도메인의 에그리거트에 추가한다고 해보자.

그러면 주문에서 `Order`에서 회원 탈퇴, 회원 가입을 제공해야 하는 아이러니함이 포함된다.

### 에그리거트 묶을 때는 생명주기를 고려하자

또한 에그리거트를 묶을 때 `A는 B를 포함한다. 그러니 A와 B는 같은 에그리거트다.` 라고 쉽게 생각하곤 한다.

`Order` 와 `OrderLine` 과의 관계를 생각해보면 생명주기를 고려하지 않았을 때 위의 개념대로 묶어도 문제가 안생긴다.

하지만 상품을 뜻하는 `Product` 와 리뷰를 뜻하는 `Review` 이 둘을 하나의 에그리거트로 묶어야 하는가?

상품 상세 페이지로 들어가면 `Review` 엔티티에 속하는 리뷰들이 쭉 있을거고, 상품 정보를 뜻하는 `Product` 엔티티에 속하는 정보도 있을거다.

그렇다고 리뷰를 생성하고 삭제하는 주기가 `Product`와 함께 진행되진 않을것이다. 

말뜻 그대로 해석해보면, 만약 둘이 하나의 에그리거트라 하고 `Product`가 에그리거트 루트이면 

"상품이 하나 추가되면 곧바로 리뷰도 생겨난다" 라는 생명주기가 공존하게 된다. 그 반대로도 말이안된다.

따라서 에그리거트를 논할때에는 비슷한 객체도 좋지만 생명주기를 잘 생각해야 한다.

---

## 에그리거트의 기능제공과 일관성 유지

에그리거트 내의 벨류타입에 대한 변경과 엔티티에 대한 변경은 무조건 에그리거트 루트를 통해서만 접근가능해야 한다.

이를 구현하기 위해서는 습관적으로 만드는 Getter & Setter를 주의해야하고, 특히나 상태를 변경하는 Setter의 경우 `private` 혹은 `protected` 를 사용하는것이 좋다.

만약에 Order의 상태를 변경하는 코드를 `setOrderState()` 로만 제공한다면, 어떤 상태에서 어떻게 변화하는지, 그리고 현재 상태에 따른 변경 제약사항등의 로직이 도메인 안에 응집되지 못하고 분산된다.

예를들어 `Order` 의 일부분이 다음과 같이 구성되어 있다고 가정 해보자.

```java
public class Order {
	
	private List<OrderLine> orderLines;
	private Money totalAmounts;
	
	public List<OrderLine> getOrderLines() {
		return orderLines;
    }
	
	public void addOrderLine(OrderLine newOrderLine) {
		this.orderLines.add(newOrderLine);
		calculateTotalAmounts();
    }
	
	private void calculateTotalAmounts() {
		totalAmounts = new Money(orderLines.stream().mapToInt(x -> x.getPrice().getValue()).sum());
    }
}
```

만약 Order 에서 List<OrderLine> 에 getter 를 통해 직접 접근할 수 있는 나머지 아래와 같은 코드를 작성하였다고 해보자.
```java
List<OrderLine> orderLines = order.getOrderLines();
orderLines.add(new OrderLine());
```

이러면 총 가격 계산에 오류가 생길 수 있다.

물론 Money와 같은 벨류에서도 불변 객체로 구현하지 않으면 동일한 이슈가 발생 할 수 있다.

따라서 **일관성 유지 측면에서도 반드시 에그리거트 루트의 기능으로서만 내부 객체를 간접적으로 접근하여야 한다.**

### 트랜잭션으로 보는 에그리거트

DB트랜잭션의 범위는 일반적으로 작으면 작을수록 좋다.

즉, 하나의 트랜잭션에서 여러 테이블을 modify 하는것보다 하나의 트랜잭션은 하나의 테이블만 관여하는것이 동시적인 처리량에 도움이 된다.

이를 에그리거트와 결합하자면 **영속화 시킬 때 하나의 에그리거트만 트랜잭션으로 범위가 지정되어야 한단 것이다.**

물론 팀의 규칙 혹은 여러 제약사항으로 인해 하나의 응용계층 기능에서 두 에그리거트를 `modify` 해야할 수 있다.

그렇다고 하나의 에그리거트 내에서 또다른 에그리거트의 수정을 요청하는것은 에그리거트 사이의 경계가 깨져 애그리거트 사이의 결합도를 높히기도 하고 트랜잭션의 범위가 너무 넓어진다.

어쩔 수 없다면 결합도라도 낮추기 위해 하나의 응용계층 기능 내에서 두 도메인 애그리거트에 대해 별도로 `modify` 시킨다.

트랜잭션까지 경계를 짓고 싶다면, 동기 혹은 비동기 방식의 이벤트를 전달하는것으로도 방법이 된다.

---

## 레포지토리와 에그리거트

에그리거트 하나를 저장하는 레포지토리는 에그리거트내부 벨류타입과 엔티티를 모두 저장해야 한다.

물론 물리적으로 `Order`와 `OrderLine`이 별도의 테이블로 존재한다고 해도

인터페이스에서는 에그리거트 단위로 `save()` 메소드를 제공해야한다.

에그리거트를 찾는 메소드도 에그리거트의 루트 엔티티의 식별자를 통해 찾아야 하며,

`Order`를 찾는데 있어서 `OrderLine` 과 같은 내부요소중 어느것도 빠짐없이 함께 조회되어야 한다.

---

## 에그리거트 내 참조

주문 에그리거트의 루트 엔티티인 `Order`에는 주문자를 뜻하는 `Orderer`가 있다.

그리고 회원 에그리거트의 루트 엔티티는 `Member`가 있다고 가정해보자.

일반적으로 `Order`의 `Orderer`는 다음과 같이 구성될 것이다.

```java
public class Order {
	
	private List<OrderLine> orderLines;
	private ShippingInfo shippingInfo;
	private Member orderer;
}
```

이러면 에그리거트 사이의 경계가 모호해지기도하고, 일반적으로 레포지토리는 루트 엔티티의 식별자를 통해 에그리거트 째로 조회한다.

다음과 같은 기능이 제공되어야 한다고 생각해보자.

"주문자는 출고 전에 배송지를 변경할 수 있다. 단, 배송지가 기존의 사용자의 기본 배송지와 일치하지 않는다면 기본 배송지를 현재 배송지로 수정한다."

위의 상태에서 이것을 구현한다고 하면 다음과 같은 코드가 발생된다.

```java
// order 응용계층
public class ChangeShippingService {
	
	private OrderRepository orderRepository;
	
	public void changeShippingService(long orderId, ShippingInfo newShippingInfo) {
		
		// Order 도메인을 들고온다.
		Order order = orderRepository.findById(orderId);
		
		// 배송지 변경에 대한 도메인로직을 실행시킨다.
		order.changeShippingInfo(newShippingInfo);
    }
}
```
```java
public class Order {
	
	private List<OrderLine> orderLines;
	private ShippingInfo shippingInfo;
	private Member orderer;
	private OrderStatus status;
	
	public void changeShippingInfo(ShippingInfo newShippingInfo) {
		// 출고 전 상태인지 파악한다.
		checkCanShippingChange();
        // 출고 전 상태라면 배송지 정보를 변경한다.
		setShippingInfo(newShippingInfo);
		
		// 회원의 기본배송지 변경 로직을 실행시킨다.
        orderer.changeDefaultShippingInfo(this.shippingInfo);
    }
	
	private void checkCanShippingChange() {
		if (status != OrderStatus.PREPAREING && status != OrderStatus.PAYMENT_WATIING) {
			throw new IllegalStateException();
		}
    }
}
```

하나의 에그리거트 내에서 또다른 에그리거트의 도메인로직을 실행시킨다.

이것은 곧 `Member`의 변경이 발생하면 `Order` 또한 변경이 발생되어야 함 알 수 있다.

또한 연관객체를 그대로 참조하는 코드에서는 함께 조회하는 전략적인 고민도 해야한다. (Lazy or Eager)

어떤 조회를 위한 기능의 경우에서는 한번에 다 들고와야 하지만, 변경을 위한 기능에서는 변경에 해당하는 범위까지만 조회하는것이 유리하기 때문이다.

이런 경우 에그리거트간의 경계를 명확하게 하기 위해서 **ID만을 가지는것으로 변경하게 된다.**

그러면 다음과 같이 로직이 변경된다.

```java
public class Order {

	private List<OrderLine> orderLines;
	private ShippingInfo shippingInfo;
	private long memberId; // ID 만을 가지도록
}
```
```java
// order 응용계층
public class ChangeShippingService {
	
	private OrderRepository orderRepository;
	private MemberRepository memberRepository;
	
	public void changeShippingService(long orderId, ShippingInfo newShippingInfo) {
		
		// Order 도메인을 들고온다.
		Order order = orderRepository.findById(orderId);
		
		// 배송지 변경에 대한 도메인로직을 실행시킨다.
		order.changeShippingInfo(newShippingInfo);
		
		// member 레포지토리에서 도메인을 조회한다.
        // 응용계층을 통해서 다른 도메인 모델에 접근이 자유로울 수있기 때문이다.
        Member member = memberRepository.findById(order.getOrderer());
		member.changeShippingInfo(order.getShippingInfo());
    }
}
```
이렇게 하면 명확하게 두 에그리거트 간에 결합이 줄어들고 에그리거트 안에서의 응집도는 높아진다.

### ID 참조를 통한 조회

이렇게 ID참조를 통해 결합도도 낮추고 구현도 쉬워졌다.

하지만 객체들간의 연관관계에서 특히 조회기능을 구현할 때에 ID참조만으로 오로지 처리하려 하면 N+1 문제가 될 수 있다.

예를들어 어떤 "주문자의 모든 주문에 있어서 첫 주문항목의 상품들을 조회"한다고 가정해보자. 

ID 참조만으로 처리한다면 아래의 코드가 발생한다.

```java
List<Order> orders = orderRepository.findOrdersByOrder(ordererId);

orders.stream().map(order -> {
	long productId = order.getFirstOrderLine().getProductId();
	productRespository.findById(productId);
    //... 조회 포장용 코드
})

```
하나의 Order 애그리거트 조회 쿼리가 발생할 것이고, 이어서 Product는 별도의 에그리거트이기에 N 번의 Product 에그리거트 조회쿼리가 발생한다.

즉 N+1 문제가 발생하는것이다.

이는 마치 응용계층 내에서 지연로딩을 일으키는 것이기에, 즉시로딩으로 바꾸면 해결할 수 있다.

```java
public class OrderLine {
	private Product product; // 결국 다시 에그리거트 간의 참조가 발생한다.
}
```

아까전과 동일하게 에그리거트간의 경계가 모호해진다.

이런상황은 대부분 조회기능을 구현하는데에서 발생한다. **여기서 말하는 조회 기능은 단순히 에그리거트를 조회하는 단순 조회를 뜻하는게 아니다.**

**응용계층의 하나의 기능으로서 조회기능을 말하는것이다.**

ID참조를 통한 에그리거트 간의 관계로서 해결하기 보다는 DAO라는 개념을 도입하여 둘 이상의 애그리거트를 하나의 쿼리로 처리하도록 전용 메소드를 만들면 된다.







