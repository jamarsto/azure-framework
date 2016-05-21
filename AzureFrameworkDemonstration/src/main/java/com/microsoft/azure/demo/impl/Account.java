package com.microsoft.azure.demo.impl;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.demo.DepositedFunds;
import com.microsoft.azure.demo.SavedAccount;
import com.microsoft.azure.demo.internal.InternalDepositFunds;
import com.microsoft.azure.framework.domain.aggregate.AbstractAggregate;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.SnapshotEvent;

public final class Account extends AbstractAggregate {
	private List<Transaction> transactions = new ArrayList<Transaction>();

	public Boolean apply(final DepositedFunds event) {
		final Transaction transaction = inject(new Transaction());
		transaction.apply(event);
		transactions.add(transaction);
		return Boolean.TRUE;
	}

	public List<Event> decide(final InternalDepositFunds command) {
		final List<Event> results = new ArrayList<Event>();
		results.add(new DepositedFunds(command.getAmount()));
		return results;
	}

	@Override
	protected SnapshotEvent snapshot() {
		return new SavedAccount(transactions);
	}
}
