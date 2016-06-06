package com.microsoft.azure.demo.view.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

public final class TransactionBean {
	private UUID accountId;
	private BigDecimal amount;
	private Calendar createdDateTime;
	private Character type;

	public TransactionBean() {
	}

	public TransactionBean(final UUID accountId, final Character type, final Calendar createdDateTime,
			final BigDecimal amount) {
		this.accountId = accountId;
		this.type = type;
		this.createdDateTime = createdDateTime;
		this.amount = amount;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Calendar getCreatedDateTime() {
		return createdDateTime;
	}

	public Character getType() {
		return type;
	}
}
