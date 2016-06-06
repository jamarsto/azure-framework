package com.microsoft.azure.demo.view.impl;

import java.math.BigDecimal;
import java.util.UUID;

public final class AccountBean {
	private BigDecimal balance;
	private UUID id;

	public AccountBean() {
	}
	
	public AccountBean(final UUID id, final BigDecimal balance) {
		this.id = id;
		this.balance = balance;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public UUID getId() {
		return id;
	}
}
