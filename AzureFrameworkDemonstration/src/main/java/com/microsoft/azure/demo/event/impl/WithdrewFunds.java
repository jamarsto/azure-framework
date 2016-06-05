package com.microsoft.azure.demo.event.impl;

import java.math.BigDecimal;
import java.security.InvalidParameterException;

import com.microsoft.azure.framework.domain.event.AbstractEvent;
import com.microsoft.azure.framework.domain.event.Event;

public class WithdrewFunds extends AbstractEvent implements Event {
	private static final long serialVersionUID = 1L;
	private BigDecimal amount;

	public WithdrewFunds() {
	}

	public WithdrewFunds(final BigDecimal amount) {
		if(amount == null) throw new InvalidParameterException("Amount is required.");
		if(amount.compareTo(new BigDecimal("0.00")) <= 0) throw new InvalidParameterException("Amount must be positive");

		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
