package com.microsoft.azure.demo.view.viewmanager.view.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.microsoft.azure.demo.event.impl.DepositedFunds;
import com.microsoft.azure.demo.event.impl.WithdrewFunds;
import com.microsoft.azure.framework.domain.event.Event;

@Entity
@Table(name = "TRANSACTION_VIEW")
public final class TransactionView {
	private static final Character DEPOSIT = 'D';
	private static final Character WITHDRAWAL = 'W';
	@Column(name = "AGGREGATE_ID", updatable = false)
	private UUID aggregateId;
	@Column(name = "AMOUNT", updatable = false)
	private BigDecimal amount;
	@Id
	@Column(name = "ID", insertable = false, updatable = false)
	private UUID id;
	@Column(name = "TRANSACTION_TYPE", updatable = false)
	private Character type;
	@Column(name = "TRANSACTION_CREATED", updatable = false)
	private Calendar createdDateTime;
	
	public TransactionView() {
	}

	private TransactionView(final UUID aggregateId, final Event event) {
		this.aggregateId = aggregateId;
		this.createdDateTime = event.getCreatedDateTime();
	}

	public TransactionView(final UUID aggregateId, final DepositedFunds event) {
		this(aggregateId, (Event)event);
		this.type = DEPOSIT;
	}
	
	public TransactionView(final UUID aggregateId, final WithdrewFunds event) {
		this(aggregateId, (Event)event);
		this.amount = event.getAmount();
		this.type = WITHDRAWAL;
	}
}