package com.microsoft.azure.demo.command.impl;

import java.util.UUID;

import com.microsoft.azure.framework.command.AbstractCommand;

public final class CreateAccount extends AbstractCommand {
	private UUID aggregateId;

	public CreateAccount() {
	}

	@Override
	public UUID getAggregateId() {
		return aggregateId;
	}

	@Override
	public void validate() {
		preconditionService.requiresNotNull("Account ID is required.", aggregateId);
	}
}
