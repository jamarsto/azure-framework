package com.microsoft.azure.demo.command.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.command.DepositFunds;
import com.microsoft.azure.framework.command.AbstractCommand;
import com.microsoft.azure.framework.precondition.PreconditionService;

public final class DepositFundsCommand extends AbstractCommand implements DepositFunds {

	public static final class Builder implements DepositFunds.Builder {
		private UUID aggregateId;
		private BigDecimal amount;
		@Autowired
		private PreconditionService preconditionService;

		@Override
		public DepositFunds build() {
			preconditionService.requiresNotNull("Account ID is required.", aggregateId);
			preconditionService.requiresNotNull("Amount is required.", amount);

			return new DepositFundsCommand(this);
		}

		@Override
		public Builder buildAccountId(final UUID aggregateId) {
			preconditionService.requiresNotNull("Account ID is required.", aggregateId);

			this.aggregateId = aggregateId;
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

	private UUID aggregateId;
	private BigDecimal amount;

	public DepositFundsCommand() {
	}

	private DepositFundsCommand(final Builder builder) {
		this.aggregateId = builder.aggregateId;
		this.amount = builder.amount;
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
