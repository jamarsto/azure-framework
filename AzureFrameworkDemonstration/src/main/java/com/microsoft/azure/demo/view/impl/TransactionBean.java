package com.microsoft.azure.demo.view.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class TransactionBean {
	@XmlElement
	private UUID accountId;
	@XmlElement
	private BigDecimal amount;
	@XmlElement
	private Calendar createdDateTime;
	@XmlElement
	private UUID id;
	@XmlElement
	private Character type;

	public TransactionBean() {
	}

	public UUID getAccountId() {
		return accountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Calendar getCreatedDateTime() {
		return createdDateTime;
	}

	public UUID getId() {
		return id;
	}

	public Character getType() {
		return type;
	}

	public void setAccountId(final UUID accountId) {
		this.accountId = accountId;
	}

	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	public void setCreatedDateTime(final Calendar createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setType(final Character type) {
		this.type = type;
	}
}
