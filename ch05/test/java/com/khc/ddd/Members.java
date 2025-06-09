package com.khc.ddd;

import com.khc.ddd.app.member.domain.Member;
import com.khc.ddd.app.member.domain.MemberRepository;
import com.khc.ddd.app.member.domain.value.MemberId;

public class Members {

	public static Member getBasicMember() {
		return new Member(MemberRepository.nextId(), "김회창");
	}
}
