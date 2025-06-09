package com.khc.ddd.app.order.domain.value;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address implements Serializable {

	private String address1;
	private String address2;
	private String zipcode;

	public Address(String address1, String address2, String zipcode) {
		this.address1 = address1;
		this.address2 = address2;
		this.zipcode = zipcode;
	}

	protected Address() {

	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getZipcode() {
		return zipcode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Address address = (Address)o;
		return Objects.equals(getAddress1(), address.getAddress1()) && Objects.equals(getAddress2(),
			address.getAddress2()) && Objects.equals(getZipcode(), address.getZipcode());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAddress1(), getAddress2(), getZipcode());
	}
}
