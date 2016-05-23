package com.microsoft.azure.demo.event.impl;

import java.math.BigDecimal;

import com.microsoft.azure.framework.domain.event.Event;

public class DepositedFunds implements Event {
	private static final long serialVersionUID = 1L;
	private BigDecimal amount;

	public DepositedFunds() {
	}

	public DepositedFunds(final BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
