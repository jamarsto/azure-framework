package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.DepositFunds;
import com.microsoft.azure.framework.command.AbstractCommand;
import com.microsoft.azure.framework.precondition.PreconditionService;

public final class DepositFundsCommand extends AbstractCommand implements DepositFunds {

	public static final class Builder implements DepositFunds.Builder {
		private BigDecimal amount;
		private UUID entityID;
		@Autowired
		private PreconditionService preconditionService;

		@Override
		public DepositFunds build() {
			preconditionService.requiresNotNull("Account ID is required.", entityID);
			preconditionService.requiresNotNull("Amount is required.", amount);

			return new DepositFundsCommand(this);
		}

		@Override
		public Builder buildAccountId(final UUID entityID) {
			preconditionService.requiresNotNull("Account ID is required.", entityID);

			this.entityID = entityID;
			return this;
		}

		@Override
		public Builder buildAmount(final BigDecimal amount) {
			preconditionService.requiresNotNull("Amount is required.", amount);

			this.amount = amount;
			return this;
		}
	}

	@Component
	public static final class BuilderFactory extends AbstractCommand.AbstractBuilderFactory
			implements DepositFunds.BuilderFactory {
		@Override
		public Builder create() {
			return inject(DepositFundsCommand.Builder.class, new Builder());
		}
	}

	private final BigDecimal amount;
	private final UUID id;

	private DepositFundsCommand(final Builder builder) {
		this.id = builder.entityID;
		this.amount = builder.amount;
	}

	@Override
	public UUID getAggregateID() {
		return id;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
