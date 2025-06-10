## 표현계층

표현계층은 사용자의 요청에 들어간 값들 (파라미터나 HttpHeader 등) 을 기능을 제공하기 위한 서비스 메소드의 파라미터 형식에 변형하고

사용자가 원하는 응답의 형식 (Json 이나 HTML) 에 맞춰서 반환하는 계층이다.

표현계층의 책임은 크게 3가지로 나뉜다.

1. 사용자에게 시스템의 흐름(화면 등)을 제공
2. 사용자의 요청을 응용계층의 메소드 파라미터에 맞게 바꾸고 그 결과를 반환한다.
3. 사용자의 세션을 관리한다.

1번의 경우 요청에 따라 알맞은 뷰를 보여줌으로서 기능을 수행할 시나리오로 사용자를 유도하는 것이다.

2번의 경우 Spring MVC 에서 제공하는 JSON을 객체에 바로 매핑하는 기능을 사용하면 편하게 서비스에서 필요한 객체형식으로 사용자 요청을 받을 수 있다.

## 응용 계층

응용계층은 표현계층과 도메인계층 사이를 연결시켜주는 역할처럼 보인다.

응용계층에서는 표현계층 입장에서는 사용자에게 필요한 기능을 수행하는 역할을 해야하고

**그 기능을 위해 협력하는 도메인모델들을 레포지토리를 통해 조회해서 도메인로직들을 호출해야 한다.**

그리고 DB 와 같은 구현기술들과의 협력도 응용계층에서 이어지기 때문에, 

**애그리거트의 영속화 및 조회에 대해 트랜잭션도 제공해야 한다.**

몇가지 주의할 사항이 있다.

1. 도메인로직을 응용계층에 포함해선 안된다.
2. 표현계층의 의존성을 가지면 안된다.

1번의 경우 예를들어 사용자의 비정상적인 이용정지를 해제하는 서비스가 있다고 가정하자.

사용자를 뜻하는 도메인모델이 `Member` 이고 이는 패스워드를 상태로 갖고있다고 가정하자.

그리고 비정상적인 이용정지를 해제하기 위해서는 패스워드가 일치하는지 검증해야 한다고 가정하자.

```java
public class Member {
	
	private String password;
	private MemberState state;
	
	public void unban() {
		// 이용정지를 해제하기 위한 도메인 로직
    }
	
	public enum MemberState {
		BANNED, NORMAL
    }
}
```
```java
public class MemberService {
	
	private MemberRepository memberRepository;
	
	public void unbanMember(BannedMemberRequest req) {
		Member member = memberRepository.findById(req.getMemberId());
		
        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
			throw new PasswordMismatchException();
		}
		
		member.unban();
    }
}
```

여기까지는 문제없다. 하지만 패스워드를 검증하는 저 코드의 중복이 발생할 수 있다.

```java
public class MemberService {
	
	private MemberRepository memberRepository;
	
	public void unbanMember(BannedMemberRequest req) {
		Member member = memberRepository.findById(req.getMemberId());
		
        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
			throw new PasswordMismatchException();
		}
		
		member.unban();
    }
	
	public void login(LoginRequest req) {
		Member member = memberRepository.findById(req.getMemberId());

		if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
			throw new PasswordMismatchException();
		}
		// 중복 발생!

		member.login();
    }
}
```

즉, 도메인에 응집되어야할 도메인로직이 응용계층까지 침범해있어서 중복된 코드라인이 빈번하게 발생한다.

이를 방지하기 위해 패스워드 일치검사를 도메인모델로 옮기자.

```java
public class Member {
	
	public void login(LoginRequest req, PasswordEncoder encoder) {
		passwordMatch(encoder, req.getPassword());
		// 그밖의 로그인 도메인로직들
    }
	
	public void unban(BannedMemberRequest req, PasswordEncoder encoder) {
		passwordMatch(encoder, req.getPassword());
		// 그밖의 이용정지 해제 도메인 로직들
    } 
	
	private boolean passwordMatch(PasswordEncoder encoder, String inputPassword) {
		return encoder.matches(password, inputPassword);
    }
}
```

```java
public class MemberService {

	private MemberRepository memberRepository;
	private PasswordEncoder encoder;
	
	
	public void unbanMember(BannedMemberRequest req) {
		Member member = memberRepository.findById(req.getMemberId());
		member.unban(req, encoder);
	}

	public void login(LoginRequest req) {
		Member member = memberRepository.findById(req.getMemberId());
		member.login(req, encoder);
	}
}
```

이전 상황에 비해 중복되는 로직도 확 줄어들었고, 무엇보다도 `Member` 도메인 객체에 대한 응집력이 많이 올라갔다.

따라서 **도메인로직을 응용계층에 분산하게 되면 응집력도 낮아지고 하나의 기능을 파악하기 위해 두 계층을 왔다갔다 해야 한다.**

2번의 경우에는 `HttpSevletRequest` 나 `Cookie` 와 같은 객체들을 직접적으로 응용계층에서 사용하는 경우가 자주 있다.

엄연히 **위의 객체들은 사용자의 요청정보가 그대로 담겨있는 표현계층에 속하기 때문에** 응용계층에 그대로 넘기지 않고, 필요한 값만을 전달하는 방식을 추천한다.

### 응용계층과 인터페이스

인터페이스를 응용계층을 구현할때 사용해야 하는가 마는가가 주된 주제가 될 수 있다.

만약에 해당 추상화를 통해 TDD 와 같은 Mock 객체를 만들어야 하는 경우 이거나, 여러 응용계층의 구현 객체를 하나의 추상화로 부터 만들어야 한다면 인터페이스가 필요할 수 있다.

그렇지만 TDD 와 같은 테스트를 위해 모킹된 객체가 필요하다 하더라도, Mockito 같은 라이브러리를 위해 쉽게 Mock 객체를 만들 수 있어서 요즘에는 테스트 용이성을 위해서라면 근거가 약하다.

따라서 구현객체가 많은 경우가 있지않는 이상 생산성 측면에서도 굳이 인터페이스를 두지 않는것도 하나의 방법이 될 수 있다.

---

## 값 검증

표현계층과 응용계층 두 곳에서 모두 값 검증을 할 수 있다.

주로 표현계층에서는 값의 범위나 값의 형식 등을 검사할 수 있다.

응용계층에서 말하는 검증은 논리적인 검증이 이에 속한다. 데이터의 존재유무 등을 예로들수 있다.

두 곳에서 나눠서 각자의 역할에 맞게 감 검증 로직을 만들 수 있지만 응용계층 하나에서 표현계층의 값검증 역할까지 수행하는것도 하나의 방법이다.


