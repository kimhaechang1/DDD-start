## 레포지토리 구현

기본적으로 도메인 객체를 얻어오는 레포지토리들은 인터페이스이며 도메인계층에 위치하고

애그리거트 단위로 save 하는 메소드와 find 하는 메소드를 가진다.

```java
public interface SomeRepository {
	void save(Entity e);
	Entity findById(EntityId id);
}
```

### JPA 의 EntityManager 를 활용한 구현

`@PersistenceContext` 어노테이션으로 `EntityManager` Bean을 얻어서 구현한다.

`ch04.domain.OrderRepository` 가 기본적인 `Order` 에그리거트의 레포지토리이며

`ch04.infra.JpaOrderRepository`를 통해 실제 구현기술로서 JPA를 활용하여 영속화를 구현한다.

테스트를 위한 클래스는 `JpaOrderRepositoryTest` 이다.

### Spring Data JPA 를 활용한 구현

Springboot를 통해 개발하는 환경이라면 JPA 를 편하게 사용하기 위해서 Spring Data JPA 를 사용한다.

Spring Data JPA 는 기본적인 JPA 에서 각종 API 들의 구현을 도와준다.

따라서 도메인 계층의 Repository 실제 구현을 도와주는 것이다.

`ch04.domain.springdatajpa.OrderRepository` 가 Spring Data JPA 를 활용하여 만든 레포지토리이고

관련한 테스트 클래스는 `SpringDataJpaOrderRepositoryTest` 이다.

---

## 매핑구현

기본적으로 엔티티 타입에는 JPA 에서 `@Entity`를 사용한다.

벨류타입에는 JPA 에서 `Embedded` 와 `Embeddable` 을 사용한다.

### JPA 에서 엔티티 접근방식: Access

JPA 에서 엔티티에 접근할 때 리플렉션을 활용한 `필드` 접근과 `Getter & Setter`를 통한 프로퍼티 접근 방식이 있다.

`@Access(AccessType.FIELD)` 와 `@Access(AccessType.PROPERTY)`이다.

만약 `@Access` 을 사용하지 않는다면 엔티티의 `@Id` 어노테이션이 `getter & setter` 쪽에 있는가 `field` 에 있는가에 따라 달라진다.

**대부분의 상황에서는 필드 사용을 추천한다.**

왜냐하면 게터 세터를 사용하는 순간부터 도메인 로직만을 public 메소드로 포함해야하는 도메인 객체의 특징이 깨지고

특히나 세터의 경우에서는 public 인 경우 데이터의 변조를 가할 수 있게 되어 캡슐화가 깨지게된다.

### 벨류타입 매핑: EmbeddedId

어떤 벨류타입은 엔티티의 기본키 타입에 해당할 수 있다.

기본키 타입을 벨류타입으로 지정하면 해당 엔티티의 기본키와 관련한 각종 메소드를 제공할 수 있다.

사용법은 해당 벨류타입을 기본키로 사용할 엔티티의 필드위에 `@EmbeddedId` 로 선언하면 된다.

또한 다음의 스펙을 갖출것을 권장한다.

1. Serializable을 구현해야 한다.
2. 기본 생성자를 가져야 한다.
3. hashCode 와 equals 를 재정의 해야한다.

### 벨류타입 매핑: 1:1 관계에서 별도의 테이블로 매핑하고 싶을 때

기본적으로 하나의 에그리거트 내에는 대부분의 경우 하나의 엔티티와 수많은 벨류로 이루어진다.

만약 하나의 **에그리거트 내에 다른 엔티티가 필요해 보이는 경우 정말 라이프사이클이 함께 이루어지는지 고민**해야 한다.

물론 진짜 다른 엔티티를 포함하는 경우라면 엔티티간의 연관관계를 표현하여 별도 테이블로 분리해야한다.

하지만 벨류타입을 저장할 때에도 별도 테이블을 사용할 수 있다.

`@SecondaryTable` 을 활용하면 1대1 관계에 놓인 벨류타입을 물리적으로 별도 테이블에 저장할 수 있다.

> case ) Article 이라는 엔티티가 있고 ArticleContet라는 벨류타입을 별도 테이블에 저장하는 경우 Article 엔티티 디자인
> ```java 
> @Entity
> @SecondaryTable(
>   name="article_content",
>   pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")
> )
> public class Article {
>   
>    @Id
>    @GeneratedValue(strategy = GenerationType.IDENTITY)
>    private Long id;
>    
>    @AttributeOverrides(
>       @AttributeOverride(
>           name = "content",
>           column = @Column(table = "article_content", name = "content")
>       ),
>       @AttributeOverride(
>           name = "contentType",
>           column = @Column(table = "article_content", name = "content_type")
>       )
>    )
>    @Embedded
>    private ArticleContent articleContent;
> }
>```

`@PrimaryKeyJoinColumn`은 1대1 관계에서 보조테이블의 어떤 컬럼명과 주 테이블의 pk 와 연관관계를 맺을지 정한다.

`@AttributeOverride`는 원래는 특정 클래스의 필드 변수명을 테이블에 삽입할때 컬럼명을 지정하고 싶으면 사용한다.

단점이라면 Article에 대해서 목록조회를 하는 경우에도 ArticleContent 가 모조리 조인되는 문제가 있다.

### 벨류타입 매핑: 여러 필드를 하나의 컬럼으로 매핑하고 싶을 때 & 전처리가 필요할 때

때에 따라 클래스에서 여러 필드를 하나의 의미를 부여해서 컬럼에 저장하고 싶을 때가 있다.

혹은 DB에 삽입할때 혹은 DB에서 조회할때 전처리를 하고싶을 수 있다.

이럴 때 사용하는것이 `@Converter` 와 `@Convert` 그리고 `AttributeConverter<V, C>` 를 사용할 수 있다.

먼저 사용할 컨버터를 `AttributeCovnerter<ValueType, ColumnType>` 인터페이스의 구현클래스로 만들고

`@Converter` 어노테이션을 붙이는데, `autoApply` 속성값을 true로 주면 모든 `ValueType` 에 대해서 적용되고

`ValueType` 을 사용하고 있는 특정 필드에서만 적용하고 싶다면 `false`로 준 다음 `@Convert`의 `converter`속성값을 지정해주면 된다.

---

## 에그리거트 영속성 전파

에그리거트는 다음과 같은 특징을 가진다.

1. 에그리거트 내에 모든 객체는 에그리거트 루트 엔티티가 저장될때 같이 저장되어야 한다.
2. 에그리거트 내에 모든 객체는 에그리거트 루트 엔티티가 삭제될 때 모두 삭제되어야 한다.

이런 기능을 구현하기 위해서 JPA 엔티티에서는 `CascadeType.PERSIST`, `CascadeType.REMOVE` 를 `@OneToMany` 와 `@OneToOne` 에서 지원한다.

---

## 식별자 생성 기능

식별자를 생성하는 방법에는 크게 3가지 방법이 있다.

1. DB 의 생성방식을 그대로 따르기
2. 어플리케이션 도메인계층에서 생성하기
3. 사용자로 부터 입력받기

1번의 방법은 `@GenerationValue(strategy = GenerationType.IDENTITY)` 가 있다.

2번의 경우에는 크게 레포지토리의 `nextId()` 와 같은 메소드 만들기 와 도메인 서비스로 분리하기 라는 방법이 있다.

특히 도메인 서비스의 경우에는 도메인 계층에서 도메인 객체를 위한 별도의 서비스로 분리하는것이다.

```java
// domain 패키지 속
public class OrderIdService {
	
	public OrderId create() {
		
    }
}
```

왜냐하면 엔티티가 갖고있을 도메인 로직이라고 보기에는 `Id`와 같은 식별자는 엔티티의 생성과 관련되어 있기 때문이다. 

---

## DIP를 해치고 있는가?

chapter 4 에서 살펴본 레포지토리 구현과 각종 매핑에 있어서 사실상 도메인 객체에 그대로 구현기술을 적용시키고 있다.

즉, 에그리거트 내에 엔티티에 대해서는 `@Entity`를, 벨류에 대해서는 `@Embeddable, @EmbeddedId, @Embedded` 등을 직접적으로 의존하고 있다.

엄밀하게 분리하자면 도메인 계층에서 사용하는 도메인 모델 객체에 대해서 JPA 가 제공하는 각종 어노테이션을 제거해야 하고,

도메인 객체에 접근하기 위한 레포지토리 역시 인터페이스만이 도메인 계층에 존재하고 JPA 로 구현한 구현체는 인프라 계층에 만들어야 한다.

다만, DDD 저자에 의하면 도메인 객체를 구현하기 위한 기술이 JPA 말고 다른 MyBatis 와 같은 기술로 교체할 일이 거의 없었다고 한다.

따라서 어느정도의 생산성을 위한 DIP 훼손은 유연하게 받아들일 수 있는것이 좋다.



