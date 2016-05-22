package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.microsoft.azure.demo.CreateAccount;
import com.microsoft.azure.demo.DepositFunds;
import com.microsoft.azure.demo.DepositFundsService;
import com.microsoft.azure.framework.command.processor.CommandProcessor;

@Controller("depositFundsService")
@Scope("request")
public final class SimpleDepositFundsService implements DepositFundsService {
	@Autowired
	private CreateAccount.BuilderFactory createAccountBuilderFactory;
	@Autowired
	private DepositFunds.BuilderFactory depositFundsBuilderFactory;
	@Autowired
	private CommandProcessor commandProcessor;
	private BigDecimal amount;

	@Override
	public void depositFunds() {
		final CreateAccount.Builder createAccountBuilder = createAccountBuilderFactory.create();
		final DepositFunds.Builder depositFundsBuilder = depositFundsBuilderFactory.create();
		final UUID id = UUID.randomUUID();

		createAccountBuilder.buildAccountId(id);
		final CreateAccount createAccount = createAccountBuilder.build();

		depositFundsBuilder.buildAccountId(id).buildAmount(amount);
		final DepositFunds depositFunds = depositFundsBuilder.build();

		commandProcessor.doCommand(createAccount);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
		commandProcessor.doCommand(depositFunds);
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
