package com.microsoft.azure.demo.domain.event.impl;

import java.math.BigDecimal;

import com.microsoft.azure.framework.domain.event.AbstractEvent;
import com.microsoft.azure.framework.domain.event.CreatedEvent;

public class CreatedAccount extends AbstractEvent implements CreatedEvent {
	private static final long serialVersionUID = 1L;
	private BigDecimal balance = new BigDecimal("0.00");

	public CreatedAccount() {
	}

	public BigDecimal getBalance() {
		return balance;
	}
}
