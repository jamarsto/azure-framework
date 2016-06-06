package com.microsoft.azure.demo.view.impl;

import java.math.BigDecimal;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class AccountBean {
	@XmlElement
	private BigDecimal balance;
	@XmlElement
	private UUID id;

	public AccountBean() {
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public UUID getId() {
		return id;
	}

	public void setBalance(final BigDecimal balance) {
		this.balance = balance;
	}

	public void setId(final UUID id) {
		this.id = id;
	}
}
