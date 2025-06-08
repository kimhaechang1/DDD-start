package com.khc.ddd.ch04.order.domain.value;

import java.io.Serializable;
import java.util.Objects;

import com.khc.ddd.ch04.member.domain.value.MemberId;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class Orderer implements Serializable {

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "id", column = @Column(name = "orderer_id"))
	})
	private MemberId memberId;

	protected Orderer() {

	}

	public Orderer(final MemberId memberId) {
		this.memberId = memberId;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Orderer orderer = (Orderer)o;
		return memberId == orderer.memberId;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(memberId);
	}

	public MemberId getMemberId() {
		return memberId;
	}

	public static Orderer of(final MemberId memberId) {
		return new Orderer(memberId);
	}
}
