package com.khc.ddd.app.order.domain.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Money attribute) {
		return attribute.getValue();
	}

	@Override
	public Money convertToEntityAttribute(Integer dbData) {
		return new Money(dbData);
	}

}
