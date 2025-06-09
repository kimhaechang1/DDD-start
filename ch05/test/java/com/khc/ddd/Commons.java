package com.khc.ddd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.khc.ddd.app.member.domain.MemberRepository;
import com.khc.ddd.app.product.domain.ProductRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class Commons {

	@Autowired
	protected TestEntityManager entityManager;

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected ProductRepository productRepository;
}
