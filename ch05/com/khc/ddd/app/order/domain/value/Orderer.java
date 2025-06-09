package com.khc.ddd.app.order.domain.value;

import java.io.Serializable;
import java.util.Objects;

import com.khc.ddd.app.member.domain.value.MemberId;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Orderer implements Serializable {

	@AttributeOverride(name = "id", column = @Column(name = "orderer_id"))
	private MemberId memberId;

	@Column(name = "orderer_name")
	private String name;

	protected Orderer() {

	}

	public Orderer(final MemberId memberId, String name) {
		this.memberId = memberId;
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Orderer orderer = (Orderer)o;
		return Objects.equals(memberId, orderer.memberId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(memberId);
	}

	public MemberId getMemberId() {
		return memberId;
	}
}
