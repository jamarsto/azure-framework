package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.UUID;

import com.microsoft.azure.framework.domain.event.SnapshotEvent;

public class SavedAccount implements SnapshotEvent {
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final BigDecimal balance;

	public SavedAccount(final UUID id, final BigDecimal balance) {
		this.id = id;
		this.balance = balance;
	}

	public UUID getID() {
		return id;
	}

	public BigDecimal getBalance() {
		return balance;
	}
}
