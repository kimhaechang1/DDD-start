## 도메인 서비스

응용계층에서는 기능을 제공하기 위해서 협력할 도메인 객체들을 레포지토리로 부터 불러온 뒤 트랜잭션을 구현한다.

서비스는 응용계층에서만 주로 `XXXService` 라는 이름으로 클래스를 만들어서 구현하였다.

응용계층의 서비스는 다음의 책임이 있다.

1. 트랜잭션을 지원해야 한다.
2. 각종 타 계층의 기능을 조합하여 적절히 호출하는 방식으로 기능을 제공해야 한다.

즉, 응용계층의 서비스는 애그리거트 단위의 불러오기와 저장 그리고 기능구현을 위한 도메인로직 호출만이 발생할 뿐이다.

다음의 예시 상황을 고려해보자.

### 도메인 서비스: 쿠폰과 주문

```text
만약 할인 쿠폰을 주문에 적용시키는 요구사항이 들어왔다.

이를 구현시킬려면 어떤 도메인에 포함시켜야 할까?
```
쿠폰은 우선 하나의 도메인으로 볼 수 있을까?

쿠폰자체를 벨류로 보기에는 어떠한 값을 나타내는 것이 아니다.

쿠폰도 여러 의미적인 메소드를 가질 수 있다.

또한 예를들어 식별자로 구분지어야 하지, `Money` 나 `OrderLine` 과 같은 내부 필드들의 동일여부로 구분짓긴 애매하다.

그러면 쿠폰을 하나의 애그리거트라고 보고 생각했을 때,

결국 두 도메인간의 협력이 이루어져야한다.

```java
@Service
public class OrderService {

	private OrderRepository orderRepository;
	private CouponRepository couponRepository;

	public OrderService(OrderRepository orderRepository, CouponRepository couponRepository) {
		this.orderRepository = orderRepository;
		this.couponRepository = couponRepository;
	}

	@Transactional
	public void createOrder(OrderRequest req) {

		OrderNo id = OrderRepository.nextOrderNo();
		Orderer orderer = new Orderer(member.getMemberId(), member.getName());
		ShippingInfo shippingInfo = new ShippingInfo(
			new Address("대구광역시", "북구 동천로", "12345"),
			new Receiver("김회창", "010-1234-5678")
		);
		List<OrderLine> orderLines = Arrays.stream(products)
			.map(p -> new OrderLine(p.getProductId(), 1000, 1))
			.toList();

		Set<Long> couponIds = req.getCoupons();
		Order order = new Order(id, orderer, orderLines, shippingInfo, OrderState.PREPARING, couponIds);

		List<Coupon> coupons = couponRepository.findAllByIdIn(couponIds);

		for (Coupon coupon : coupons) {
			Money discounted = coupon.calculateDiscount(order.getTotalAmounts());
			order.applyDiscount(discounted);
		}
	}
}
```

응용계층의 `createOrder` 메소드에서는 대부분의 코드라인이 어떤 애그리거트를 구성하기 위한 생성 로직이거나 필요한 도메인을 위한 조회 로직인데,

```java
for (Coupon coupon : coupons) {
    Money discounted = coupon.calculateDiscount(order.getTotalAmounts());
    order.applyDiscount(discounted);
}
```

이부분만 뭔가 도메인 주도 개발스럽지 않은 절차지향적인 부분이 보이게 된다.

즉 위와같이, **둘 이상의 애그리거트들이 서로 협력해야 하며 해당 로직이 특정 애그리거트 안에 소속되기 힘들때에 사용하는것이 도메인서비스이다.**

왜 도메인이란 이름을 붙인건가? 크게보면 주문이라는 도메인 개념안에 있을만한 비즈니스 로직이기 때문이며

응용계층에서는 기능을 수행하기위한 매개체들을 조회하여 비즈니스 로직을 호출할 뿐이다.

그렇지만 저 로직이 특정 도메인 모델 객체인 애그리거트안에 소속시키기엔 애매하다.

따라서 `OrderDiscountService` 와 같은 별도의 할인로직을 처리하는 도메인 서비스를 만들고, 해당 서비스에서 로직을 수행하도록 구현한다.

```java
public class OrderDiscountService {
	
	public void discount(Order order, List<Coupon> coupons) {
		for (Coupon coupon : coupons) {
			Money discounted = coupon.calculateDiscount(order.getTotalAmounts());
			order.applyDiscount(discounted);
		}
    }
}
```
```java
@Service
public class OrderService {

	private OrderRepository orderRepository;
	private CouponRepository couponRepository;
	private OrderDiscountService orderDiscountService;

	public OrderService(OrderRepository orderRepository, CouponRepository couponRepository, OrderDiscountService orderDiscountService) {
		this.orderRepository = orderRepository;
		this.couponRepository = couponRepository;
		this.orderDiscountService = orderDiscountService;
	}

	@Transactional
	public void createOrder(OrderRequest req) {

		OrderNo id = OrderRepository.nextOrderNo();
		Orderer orderer = new Orderer(req.getMemberId(), req.getName());
		ShippingInfo shippingInfo = new ShippingInfo(
			new Address("대구광역시", "북구 동천로", "12345"),
			new Receiver("김회창", "010-1234-5678")
		);
		Set<Long> couponIds = req.getCoupons();
		List<Coupon> coupons = couponRepository.findAllByIdIn(couponIds);
		Order order = new Order(id, orderer, req.getOrderLine(), shippingInfo, OrderState.PREPARING, couponIds);
		orderDiscountService.discount(order, couponRepository.findAllByIdIn(couponIds));
		orderRepository.save(order);
	}
}
```

위와같이 응용서비스에서 직접 사용할 수도 있고, 특정 애그리거트에게 도메인 서비스를 넘겨서 사용하도록 구현할 수 있다.

여기서 중요한것은 **도메인서비스의 제공자는 응용계층이어야 한다는 점이다.**

### 도메인서비스: 외부 API 호출

외부 API (RestClient 등) 를 호출하여 타 시스템과의 연동이 필요한 경우에는

연동이 필요한 부분만을 인터페이스로 추출하여 그 구현을 인프라 계층에 맡기면 된다.

