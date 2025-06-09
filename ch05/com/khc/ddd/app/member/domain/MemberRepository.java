package com.khc.ddd.app.member.domain;

import java.util.UUID;

import org.springframework.data.repository.Repository;

import com.khc.ddd.app.member.domain.value.MemberId;
import com.khc.ddd.app.product.domain.value.ProductId;

public interface MemberRepository extends Repository<Member, MemberId> {

	Member findById(MemberId memberId);
	void save(Member member);

	public static MemberId nextId() {
		return new MemberId("member_" + UUID.randomUUID());
	}
}
