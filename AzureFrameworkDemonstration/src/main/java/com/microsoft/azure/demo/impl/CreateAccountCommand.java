package com.microsoft.azure.demo.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.CreateAccount;
import com.microsoft.azure.framework.command.AbstractCommand;
import com.microsoft.azure.framework.precondition.PreconditionService;

public final class CreateAccountCommand extends AbstractCommand implements CreateAccount {

	public static final class Builder implements CreateAccount.Builder {
		private UUID aggregateId;
		@Autowired
		private PreconditionService preconditionService;

		@Override
		public CreateAccount build() {
			preconditionService.requiresNotNull("Account ID is required.", aggregateId);

			return new CreateAccountCommand(this);
		}

		@Override
		public Builder buildAccountId(final UUID aggregateId) {
			preconditionService.requiresNotNull("Account ID is required.", aggregateId);

			this.aggregateId = aggregateId;
			return this;
		}
	}

	@Component
	public static final class BuilderFactory extends AbstractCommand.AbstractBuilderFactory
			implements CreateAccount.BuilderFactory {
		@Override
		public Builder create() {
			return inject(CreateAccountCommand.Builder.class, new Builder());
		}
	}

	private UUID aggregateId;

	public CreateAccountCommand() {
	}

	private CreateAccountCommand(final Builder builder) {
		this.aggregateId = builder.aggregateId;
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
