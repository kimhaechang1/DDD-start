## 조건에 따른 조회: Specification

기본적인 에그리거트를 통한 조회와 저장 및 업데이트는 문제가 없다.

다만 통계 데이터와 같은 복잡하고 동적인 조건절을 사용하는 조회 기능의 경우 에그리거트만을 조회하는 단순한 Repository 로 해결할 수 없다.

따라서 DAO 개념으로 진입하고, JPA 에서 지원해주는 Specification API 를 사용하면 된다.

DAO 개념으로 진입하는 순간부터는 사실상에 도메인 로직이라는 개념이 사라지고, 이에 따라 도메인 모델이란 개념도 없다.

따라서 지금부터는 DTO 혹은 단순한 엔티티(DB Table Row) 개념으로 조회한다.

### 사전준비 객체

```java
@Entity
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

	// getter
}
```

### Spring Data JPA: Specification, Sort, Pageable

기본적인 사용법은 `Specification<T>` 를 구현하는 클래스를 도메인 계층에 만들어주면 된다.

`T` 는 해당 조건의 결과로 반환되어질 객체의 타입을 뜻한다.

`Specification<T>` 인터페이스를 보면 서로다른 스펙끼리 조합할 수 있는 `and`, `or` 가 있으며

기본적으로 `toPredicate` 를 구현함으로서 조건을 구현하게 된다.

참고로 하나의 메소드만을 구현하는 인터페이스 이기에, 함수형 인터페이스가 가능하고, 이는 곧 람다표현식이 가능해진다.

```java
public class OrderSummarySpecs {

	public static Specification<OrderSummary> ordererId(String ordererId) {
		return (Root<OrderSummary> root,CriteriaQuery<?> query, CriteriaBuilder cb) ->
			cb.equal(root.get(OrderSummary_.ordererId), ordererId);
	}

	public static Specification<OrderSummary> orderDateBetween(LocalDateTime from, LocalDateTime to) {
		return (Root<OrderSummary> root,CriteriaQuery<?> query, CriteriaBuilder cb) ->
			cb.between(root.get(OrderSummary_.orderDate), from, to);
	}
}
```

여기서 끝에 언더스코어가 포함된 클래스의 static 필드를 호출하는것들이 있는데,

이는 hibernate의 modelgen 을 사용하면 컴파일/빌드시에 자동으로 `build/generated/sources/annotationProcessor/java/**` 에 생성된다.

이를 사용하는 방법은 레포지토리에서 추상 메소드를 선언할 때, 파라미터로 `Specification` 을 넣어주면 된다.

예를들어 위에서 `ordererId` 를 `Specification` 으로 받아서 처리하는 메소드를 DAO 에 정의하면 다음과 같다.

```java
import org.springframework.data.jpa.domain.Specification;

import com.khc.ddd.app.order.dto.OrderSummary;

public interface OrderSummaryDao extends Repository<OrderSummary, String> {

	List<OrderSummary> findAll(Specification<OrderSummary> spec);
}
```

```java
public void service() {
	List<OrderSummary> summary = orderSummaryDao.findAll(OrderSummarySpecs.ordererId("오더번호"));
}
```

`Sort` 를 사용하면 정렬기준도 넣을 수 있다.

`Pageable` 을 사용하면 페이지네이션이 가능한데, `Page` 로 반환받느냐 `List` 로 반환받느냐에 따라 쿼리가 달라진다.

`Page`로 반환받게 되면 기본적인 `Page` 객체 내 전체 페이지 수 라던지 페이지 내 아이템 개수 등을 제공해야 하기 때문에 `count` 쿼리가 추가로 발생한다.

`List`를 사용하면 `count` 가 발생하지 않게 할수있다.

하지만 **`Specification` 과 `Pageable` 를 함께 사용하는 경우에는 반드시 `count` 쿼리가 발생한다. 이는 내부 구현구조상 어쩔 수 없다.**

----
## JPQL 에서 DTO 로 바로 매핑하기

조회기능을 구현할 때에는 보통 엔티티 단위로 그대로 반환하지 않는다.

**즉, 필요한 컬럼들을 따로 클래스에 매핑하는경우가 대부분이다.**

이럴 때 마다 엔티티 조회 후 어플리케이션에서 DTO 로 매핑할 수 있지만, 이는 DB 에서 쓸모없는거 까지 조회하게 된다.

JPQL 에서 new 를 통한 키워드를 사용하면, 곧바로 DTO 생성자를 호출시켜 엔티티가 아닌 객체(DTO) 로 반환이 가능하다.

```java
public class OrderView {

	private final String number;

	@Override
	public String toString() {
		return "OrderView{" +
			"number='" + number + '\'' +
			", state=" + state +
			", memberName='" + memberName + '\'' +
			", memberId='" + memberId + '\'' +
			", productName='" + productName + '\'' +
			'}';
	}

	private final OrderState state;
	private final String memberName;
	private final String memberId;
	private final String productName;

	public OrderView(OrderNo number, OrderState state, String memberName, String memberId, String productName) {
		this.number = number.getNumber();
		this.state = state;
		this.memberName = memberName;
		this.memberId = memberId;
		this.productName = productName;
	}

	public String getNumber() {
		return number;
	}

	public OrderState getState() {
		return state;
	}

	public String getMemberName() {
		return memberName;
	}

	public String getMemberId() {
		return memberId;
	}

	public String getProductName() {
		return productName;
	}
}
```
위와 같은 DTO 의 필드만큼의 정보만 필요하다.

그렇지만 Order 내에는 저것말고도 더 많은 데이터가 존재한다.

```java
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
```

바로 DTO 로 매핑 할 수 있기에, Entity 들이 가져야하는 각종 연관매핑 과 조회전략등의 고민이 줄어들게 된다.

**하지만 `Specification` 과 같은 기능을 사용할 수 없게 된다.**

**그리고 반드시 풀패키지 클래스네임을 사용해야 한다.**

---
## 조회전용 엔티티

DB 에서는 FROM 절 서브쿼리에 대해서 인라인 View 라는 개념이 있다.

JPA 에서도 이와같이 뷰와 같은 성질을 띄는 엔티티를 만들 수 있는데, 이를 가능케 하는것이 `@Subselect` 이다.

사용법은 `Native Query` 를 사용하듯이 사용하면 되고, 컬럼과 필드가 잘 매핑되도록 `as` 를 사용해주자.

뷰는 말그대로 **읽기전용**이기 때문에, 변경이 발생할 수 없다.

하지만 뷰 용 엔티티는 변경이 발생할 경우 이상한 쿼리가 발생하면서 예외가 발생하게 된다.

```java
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

	orderSummaryPage.get().toList().get(0).setTotalAmounts(123414214); // 변경 발생

	entityManager.flush();
	entityManager.clear();

	assertThat(orderSummaryPage).isNotNull();
}
```

```java
org.hibernate.exception.SQLGrammarException: could not execute statement [Every derived table must have its own alias] [update ( 	select
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
) set order_date=?,orderer_id=?,orderer_name=?,product_id=?,product_name=?,receiver_name=?,state=?,total_amounts=? where number=?]
```
**`@Immutable` 어노테이션을 사용해서 변경이 발생되더라도 하이버네이트에서 무시하게 만들자.**

또한 다음과 같은 상황에서 문제가 발생할 수 있다.

```java
// 
public void service() {

	Order order = new Order(); // 새로운 오더!
	orderRepository.save(order); // 영속화!

	orderSummaryDao.findAll(OrderSummarySpecs.ordererId(order.getOrderer().getMemberId().getId()));
	// 과연 여기서 새로운 Order가 조회될까?
}
```

흐름을 보자면 조회되어야 정상이겠지만,

JPQL 도 아니고 순수 엔티티에 대한 find 이기 때문에, 쓰기지연이 발생하고 flush() 도 나중에 발생된다.

따라서 findAll() 을 할때 영속성 컨텍스트에는 save 에 따른 insert 쿼리가 flush() 되지 않아서 없다.

**이런 상황에서 싱크를 맞추기 위해 `@Synchronize({})` 어노테이션을 사용한다.**

`@Synchronize({"table_name"})` 을 사용하면 해당하는 테이블명에 변경이 같은 영속성 컨텍스트 내에서 발생한다면 

 **조회가 발생될때 flush() 를 반드시 일으킨다.**

그래서 @Subselect 를 활용해서 뷰용 엔티티를 만들려고 하면 `@Immutable`, `@Synchronize` 를 같이 세트로 사용하자.

---
## 번외: DataJpaTest 에서 insert 쿼리가 안보여요

흔히 `@DataJpaTest` 를 사용해서 `Repository` 의 확장체 혹은 구현체들을 Bean 으로 일부 등록하고 사용할 수 있다.

그런데 이상하게도 조회쿼리는 보이지만 도통 `insert` 와 `update` 쿼리는 쿼리 로깅에 안찍힌다.

그 이유는 기본적으로는 트랜잭션 메소드가 끝나면 내부적으로 커밋이 호출되고 이는 flush 를 호출하는데,

이 때 쓰기지연 저장소에 있는 SQL 들이 JDBC 를 통해 DBMS 에 전달된다. 

하지만 테스트메서드는 각각 기본적으로 종료 후 트랜잭션 롤백이 발생한다.

롤백이 발생되면, flush 가 없이 그냥 롤백만 일으키고 종료된다.

실제로 DBMS 에 전달되는 `insert update` 쿼리가 하나도 없게 되어서 안보이는것이다.

이를 명시적으로 보이게 하려면 `TestEntityManager`를 `@Autowired` 하여서 명시적으로 `flush` 를 호출하는것이 좋다.


