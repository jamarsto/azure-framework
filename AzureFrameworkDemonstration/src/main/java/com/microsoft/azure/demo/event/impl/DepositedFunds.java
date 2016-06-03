package com.microsoft.azure.demo.event.impl;

import java.math.BigDecimal;
import java.security.InvalidParameterException;

import com.microsoft.azure.framework.domain.event.AbstractEvent;
import com.microsoft.azure.framework.domain.event.Event;

public class DepositedFunds extends AbstractEvent implements Event {
	private static final long serialVersionUID = 1L;
	private BigDecimal amount;

	public DepositedFunds() {
	}

	public DepositedFunds(final BigDecimal amount) {
		if(amount == null) throw new InvalidParameterException("Amount is required.");

		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
