package com.microsoft.azure.demo.viewmanager.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.domain.aggregate.impl.Account;
import com.microsoft.azure.demo.domain.event.impl.CreatedAccount;
import com.microsoft.azure.demo.domain.event.impl.DepositedFunds;
import com.microsoft.azure.demo.viewmanager.view.impl.AccountView;
import com.microsoft.azure.framework.viewmanager.AbstractViewManager;

@Component
public final class AccountViewManager extends AbstractViewManager {

	public void apply(final UUID aggregateId, final CreatedAccount event) {
		final AccountView accountView = new AccountView(aggregateId, event);
		getEntityManager().persist(accountView);
		getEntityManager().flush();
	}

	public void apply(final UUID aggregateId, final DepositedFunds event) {
		final AccountView accountView = getEntityManager().find(AccountView.class, aggregateId);
		accountView.deposit(event.getAmount());
		getEntityManager().flush();
	}

	@Override
	protected String getAggregateClassName() {
		return Account.class.getName();
	}

	@Override
	protected String getViewName() {
		return "AccountView";
	}
}
