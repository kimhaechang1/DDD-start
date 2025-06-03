package com.khc.ddd.ch01.domainex3.entity;

import java.util.Objects;

public class Orderer {

	private int id;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Orderer orderer = (Orderer)o;
		return id == orderer.id;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
