package com.microsoft.azure.demo.aggregate.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.demo.command.impl.CreateAccountCommand;
import com.microsoft.azure.demo.command.impl.DepositFundsCommand;
import com.microsoft.azure.demo.event.impl.CreatedAccount;
import com.microsoft.azure.demo.event.impl.DepositedFunds;
import com.microsoft.azure.demo.event.impl.SavedAccount;
import com.microsoft.azure.framework.domain.aggregate.AbstractAggregate;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.SnapshotEvent;

public final class Account extends AbstractAggregate {
	private BigDecimal balance;

	public Boolean apply(final CreatedAccount event) {
		preconditionService.requiresNotNull("Event is required.", event);
		
		balance = event.getBalance();
		return Boolean.TRUE;
	}

	public Boolean apply(final DepositedFunds event) {
		preconditionService.requiresNotNull("Event is required.", event);
		
		balance = balance.add(event.getAmount());
		return Boolean.TRUE;
	}

	public Boolean apply(final SavedAccount event) {
		preconditionService.requiresNotNull("Event is required.", event);
		
		balance = event.getBalance();
		return Boolean.TRUE;
	}

	public List<Event> decide(final CreateAccountCommand command) {
		preconditionService.requiresNotNull("Command is required.", command);
		
		final List<Event> results = new ArrayList<Event>();
		results.add(new CreatedAccount());
		return results;
	}

	public List<Event> decide(final DepositFundsCommand command) {
		preconditionService.requiresNotNull("Command is required.", command);
		
		final List<Event> results = new ArrayList<Event>();
		results.add(new DepositedFunds(command.getAmount()));
		return results;
	}

	@Override
	protected SnapshotEvent snapshot() {
		return new SavedAccount(balance);
	}
}
