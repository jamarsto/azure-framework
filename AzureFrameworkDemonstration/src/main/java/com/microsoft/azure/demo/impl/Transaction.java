package com.microsoft.azure.demo.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.demo.internal.InternalDepositFunds;
import com.microsoft.azure.framework.domain.entity.AbstractEntity;
import com.microsoft.azure.framework.domain.event.Event;

public final class Transaction extends AbstractEntity {
	@SuppressWarnings("unused")
	private BigDecimal amount;
	
	public List<Event> decide(final InternalDepositFunds command) {
		final List<Event> results = new ArrayList<Event>();
		results.add(new DepositedFunds(command.getAmount()));
		return results;
	}
	
	public Boolean apply(final DepositedFunds event) {
		this.amount = event.getAmount();
		return Boolean.TRUE;
	}
}
