package com.khc.ddd.app.order.domain.value;

import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;

public class ShippingInfo {

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "address1", column = @Column(name = "shipping_addr1")),
		@AttributeOverride(name = "address2", column = @Column(name = "shipping_addr2")),
		@AttributeOverride(name = "zipcode", column = @Column(name = "shipping_zipcode"))
	})
	private Address address;

	@Embedded
	private Receiver receiver;

	protected ShippingInfo() {

	}

	public ShippingInfo(Address address, Receiver receiver) {
		this.address = address;
		this.receiver = receiver;
	}

	public Address getAddress() {
		return address;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ShippingInfo that = (ShippingInfo)o;
		return Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getReceiver(),
			that.getReceiver());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAddress(), getReceiver());
	}
}
