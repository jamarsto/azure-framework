package com.microsoft.azure.demo.view.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.view.impl.AccountBean;
import com.microsoft.azure.demo.view.persistence.AccountViewDAO;
import com.microsoft.azure.demo.viewmanager.view.impl.AccountView;

@Component
public final class SimpleAccountViewDAO implements AccountViewDAO {

	@PersistenceContext(name = "materialized-view-unit")
	private EntityManager entityManager;

	@Override
	public AccountBean getAccount(final UUID accountId) {
		final Query query = entityManager.createNamedQuery("AccountView.findById");
		final AccountView accountView = (AccountView) query.getSingleResult();
		final AccountBean accountBean = new AccountBean(accountId, accountView.getBalance());
		return accountBean;
	}

	@Override
	public List<AccountBean> getAccounts() {
		final Query query = entityManager.createNamedQuery("AccountView.findAll");
		@SuppressWarnings("unchecked")
		final List<AccountView> accountViewList = query.getResultList();
		final List<AccountBean> accountBeanList = new ArrayList<AccountBean>();
		for (final AccountView accountView : accountViewList) {
			accountBeanList.add(new AccountBean(accountView.getId(), accountView.getBalance()));
		}
		return accountBeanList;
	}
}
