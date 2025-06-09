package com.khc.ddd.app.member.domain;

import java.util.Objects;

import com.khc.ddd.app.member.domain.value.MemberId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class Member {

	@EmbeddedId
	private MemberId memberId;

	private String name;

	protected Member() {

	}

	public Member(MemberId memberId, String name) {
		this.memberId = memberId;
		this.name = name;
	}

	public MemberId getMemberId() {
		return memberId;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Member member = (Member)o;
		return Objects.equals(getMemberId(), member.getMemberId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getMemberId());
	}
}
