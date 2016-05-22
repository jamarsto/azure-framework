package com.microsoft.azure.demo.impl;

import java.util.List;

import com.microsoft.azure.framework.domain.event.CreatedEvent;

public class CreatedAccount implements CreatedEvent {
	private static final long serialVersionUID = 1L;
	private final List<Transaction> transactions;

	public CreatedAccount(final List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
}
