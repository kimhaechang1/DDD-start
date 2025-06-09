package com.khc.ddd.app.order.domain.value;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Receiver implements Serializable {

	@Column(name = "receiver_name")
	private String name;
	@Column(name = "receiver_phone")
	private String phoneNumber;

	public Receiver(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	protected Receiver() {

	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Receiver receiver = (Receiver)o;
		return Objects.equals(getName(), receiver.getName()) && Objects.equals(getPhoneNumber(),
			receiver.getPhoneNumber());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getPhoneNumber());
	}
}
