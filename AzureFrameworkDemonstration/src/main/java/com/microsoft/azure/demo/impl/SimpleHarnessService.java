package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.DepositFunds;
import com.microsoft.azure.demo.HarnessService;
import com.microsoft.azure.demo.internal.InternalDepositFunds;

@Component
@Scope("request")
public final class SimpleHarnessService implements HarnessService {
	private static final long serialVersionUID = 1L;
	@Autowired
	private DepositFunds.BuilderFactory commandBuilderFactory;
	private BigDecimal amount;

	@Override
	public void depositFunds() {
		final DepositFunds.Builder commandBuilder = commandBuilderFactory.create();

		commandBuilder.buildAccountId(UUID.randomUUID()).buildAmount(amount);

		final DepositFunds depositFunds = commandBuilder.build();

		final InternalDepositFunds privateDepositFunds = (InternalDepositFunds) depositFunds;
		System.out.println(privateDepositFunds.getAggregateID());
		System.out.println(privateDepositFunds.getAmount());
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}
}
