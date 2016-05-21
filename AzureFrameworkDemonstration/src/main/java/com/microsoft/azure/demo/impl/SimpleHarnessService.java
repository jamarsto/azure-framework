package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.DepositFunds;
import com.microsoft.azure.demo.HarnessService;
import com.microsoft.azure.demo.internal.InternalDepositFunds;
import com.microsoft.azure.framework.precondition.PreconditionService;

@Component
public final class SimpleHarnessService implements HarnessService {
	@Autowired
	private DepositFunds.BuilderFactory commandBuilderFactory;
	@Autowired
	private PreconditionService preconditionService;

	@Override
	public void depositFunds(final BigDecimal amount) {
		preconditionService.requiresNotNull("Amount must be provided.", amount);

		final DepositFunds.Builder commandBuilder = commandBuilderFactory.create();

		commandBuilder.buildAccountId(UUID.randomUUID()).buildAmount(amount);

		final DepositFunds depositFunds = commandBuilder.build();

		final InternalDepositFunds privateDepositFunds = (InternalDepositFunds) depositFunds;
		System.out.println(privateDepositFunds.getAggregateID());
		System.out.println(privateDepositFunds.getAmount());
	}

	@Override
	public void testPreconditions() {
		preconditionService.requiresNotNull("Must not be null.", "");
	}
}
