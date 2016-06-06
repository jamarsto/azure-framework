package com.microsoft.azure.demo.viewmanager.view.impl;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.microsoft.azure.demo.domain.event.impl.CreatedAccount;

@Entity
@Table(name = "ACCOUNT_VIEW")
public final class AccountView {
	@Id
	@Column(name = "ID", updatable = false)
	private UUID id;
	@Column(name = "BALANCE")
	private BigDecimal balance;

	public AccountView() {
	}

	public AccountView(final UUID id, final CreatedAccount event) {
		this.id = id;
		this.balance = event.getBalance();
	}

	public void deposit(final BigDecimal amount) {
		balance = balance.add(amount);
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public UUID getId() {
		return id;
	}
}
