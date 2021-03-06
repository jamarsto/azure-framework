package com.microsoft.azure.demo.domain.event.impl;

import java.math.BigDecimal;

import com.microsoft.azure.framework.domain.event.AbstractEvent;
import com.microsoft.azure.framework.domain.event.SnapshotEvent;

public class SavedAccount extends AbstractEvent implements SnapshotEvent {
	private static final long serialVersionUID = 1L;
	private BigDecimal balance;

	public SavedAccount() {
	}

	public SavedAccount(final BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getBalance() {
		return balance;
	}
}
