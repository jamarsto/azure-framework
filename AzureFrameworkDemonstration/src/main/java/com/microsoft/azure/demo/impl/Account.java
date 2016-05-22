package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.framework.domain.aggregate.AbstractAggregate;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.SnapshotEvent;

public final class Account extends AbstractAggregate {
	private BigDecimal balance;

	public Boolean apply(final CreatedAccount event) {
		setID(event.getID());
		balance = event.getBalance();
		return Boolean.TRUE;
	}

	public Boolean apply(final DepositedFunds event) {
		balance = balance.add(event.getAmount());
		return Boolean.TRUE;
	}

	public List<Event> decide(final CreateAccountCommand command) {
		final List<Event> results = new ArrayList<Event>();
		results.add(new CreatedAccount(command.getAggregateID()));
		return results;
	}

	public List<Event> decide(final DepositFundsCommand command) {
		final List<Event> results = new ArrayList<Event>();
		results.add(new DepositedFunds(command.getAmount()));
		return results;
	}

	@Override
	protected SnapshotEvent snapshot() {
		return new SavedAccount(getID(), balance);
	}
}
