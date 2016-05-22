package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.UUID;

import com.microsoft.azure.framework.domain.event.CreatedEvent;

public class CreatedAccount implements CreatedEvent {
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final BigDecimal balance = new BigDecimal("0.00");

	public CreatedAccount(final UUID id) {
		this.id = id;
	}

	public UUID getID() {
		return id;
	}

	public BigDecimal getBalance() {
		return balance;
	}
}
