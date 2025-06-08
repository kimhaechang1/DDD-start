package com.khc.ddd.ch04.member.domain.value;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MemberId implements Serializable {

	@Column(name = "member_id")
	private String id;

	protected MemberId() {

	}

	public MemberId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		MemberId memberId = (MemberId)o;
		return Objects.equals(id, memberId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	public static MemberId of(String id) {
		return new MemberId(id);
	}
}
