package com.microsoft.azure.demo.viewmanager.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.domain.aggregate.impl.Account;
import com.microsoft.azure.demo.domain.event.impl.CreatedAccount;
import com.microsoft.azure.demo.domain.event.impl.DepositedFunds;
import com.microsoft.azure.demo.domain.event.impl.WithdrewFunds;
import com.microsoft.azure.demo.viewmanager.view.impl.TransactionView;
import com.microsoft.azure.framework.viewmanager.AbstractViewManager;

@Component
public final class TransactionViewManager extends AbstractViewManager {
	
	public void apply(final UUID aggregateId, final CreatedAccount event) {
	}
	
	public void apply(final UUID aggregateId, final DepositedFunds event) {
		final TransactionView transactionView = new TransactionView(aggregateId, event);
		getEntityManager().persist(transactionView);
		getEntityManager().flush();
	}

	public void apply(final UUID aggregateId, final WithdrewFunds event) {
		final TransactionView transactionView = new TransactionView(aggregateId, event);
		getEntityManager().persist(transactionView);
		getEntityManager().flush();
	}

	@Override
	protected String getAggregateClassName() {
		return Account.class.getName();
	}

	@Override
	protected String getViewName() {
		return "TransactionView";
	}
}
