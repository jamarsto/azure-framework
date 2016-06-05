package com.microsoft.azure.demo.command.impl;

import java.math.BigDecimal;
import java.util.UUID;

import com.microsoft.azure.framework.command.AbstractCommand;

public final class WithdrawFunds extends AbstractCommand {
	private UUID aggregateId;
	private BigDecimal amount;

	public WithdrawFunds() {
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
		preconditionService.requiresGT("Amount must be positive", amount, new BigDecimal("0.00"));
	}
}
