package com.khc.ddd.ch01.domainex3.value;

import java.util.Objects;

public class Receiver {

	private String name;
	private String phoneNumber;

	public Receiver(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
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
