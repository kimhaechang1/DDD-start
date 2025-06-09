package com.khc.ddd.app.member.domain.value;

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

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		MemberId memberId = (MemberId)o;
		return Objects.equals(getId(), memberId.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	public static MemberId of(String id) {
		return new MemberId(id);
	}
}
