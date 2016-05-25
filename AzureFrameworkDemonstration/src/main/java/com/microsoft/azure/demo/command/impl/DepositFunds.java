package com.microsoft.azure.demo.command.impl;

import java.math.BigDecimal;
import java.util.UUID;

import com.microsoft.azure.framework.command.AbstractCommand;

public final class DepositFunds extends AbstractCommand {
	private UUID aggregateId;
	private BigDecimal amount;

	public DepositFunds() {
	}

	@Override
	public UUID getAggregateId() {
		return aggregateId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public void validate() {
		preconditionService.requiresNotNull("Account ID is required.", aggregateId);
		preconditionService.requiresNotNull("Amount is required.", amount);
	}
}
