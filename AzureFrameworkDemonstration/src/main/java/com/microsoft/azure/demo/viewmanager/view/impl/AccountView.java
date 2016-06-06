package com.microsoft.azure.demo.viewmanager.view.impl;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.microsoft.azure.demo.event.impl.CreatedAccount;

@NamedQueries({ @NamedQuery(name = "AccountView.findAll", query = "SELECT a FROM AccountView a"),
		@NamedQuery(name = "AccountView.findById", query = "SELECT a FROM AccountView a WHERE a.aggregateId = :accountId") })
@Entity
@Table(name = "ACCOUNT_VIEW")
public final class AccountView {
	@Id
	@Column(name = "AGGREGATE_ID", updatable = false)
	private UUID aggregateId;
	@Column(name = "BALANCE")
	private BigDecimal balance;

	public AccountView() {
	}

	public AccountView(final UUID aggregateId, final CreatedAccount event) {
		this.aggregateId = aggregateId;
		this.balance = event.getBalance();
	}

	public void deposit(final BigDecimal amount) {
		balance = balance.add(amount);
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public UUID getId() {
		return aggregateId;
	}
}
