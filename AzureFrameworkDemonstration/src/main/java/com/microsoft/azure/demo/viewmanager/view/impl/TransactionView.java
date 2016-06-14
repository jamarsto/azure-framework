package com.microsoft.azure.demo.viewmanager.view.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.microsoft.azure.demo.domain.event.impl.DepositedFunds;
import com.microsoft.azure.demo.domain.event.impl.WithdrewFunds;
import com.microsoft.azure.framework.domain.event.Event;

@Entity
@Table(name = "TRANSACTION_VIEW")
public final class TransactionView {
	private static final Character DEPOSIT = 'D';
	private static final Character WITHDRAWAL = 'W';
	@Column(name = "AGGREGATE_ID", updatable = false)
	private String aggregateId;
	@Column(name = "AMOUNT", updatable = false)
	private BigDecimal amount;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", insertable = false, updatable = false)
	private UUID id;
	@Column(name = "TRANSACTION_TYPE", updatable = false)
	private Character type;
	@Column(name = "TRANSACTION_CREATED", updatable = false)
	private Calendar createdDateTime;

	public TransactionView() {
	}

	private TransactionView(final UUID aggregateId, final Event event) {
		this.aggregateId = aggregateId.toString();
		this.createdDateTime = event.getCreatedDateTime();
	}

	public TransactionView(final UUID aggregateId, final DepositedFunds event) {
		this(aggregateId, (Event) event);
		this.amount = event.getAmount();
		this.type = DEPOSIT;
	}

	public TransactionView(final UUID aggregateId, final WithdrewFunds event) {
		this(aggregateId, (Event) event);
		this.amount = event.getAmount();
		this.type = WITHDRAWAL;
	}
	
	public UUID getId() {
		return id;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public Calendar getCreatedDateTime() {
		return createdDateTime;
	}
	
	public Character getType() {
		return type;
	}
}
