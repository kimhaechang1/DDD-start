package com.khc.ddd.ch01.domainex3.value;

import java.util.Objects;

public class ShippingInfo {

	private Address address;
	private Receiver receiver;

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
