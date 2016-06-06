package com.microsoft.azure.demo.view.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.view.impl.TransactionBean;
import com.microsoft.azure.demo.view.persistence.TransactionViewDAO;
import com.microsoft.azure.demo.viewmanager.view.impl.TransactionView;

@Component
public final class SimpleTransactionViewDAO implements TransactionViewDAO {

	@PersistenceContext(name = "materialized-view-unit")
	private EntityManager entityManager;

	@Override
	public TransactionBean getTransaction(final UUID accountId, final UUID transactionId) {
		final Query query = entityManager.createNamedQuery("TransactionView.findById");
		query.setParameter("accountId", accountId);
		query.setParameter("transactionId", transactionId);
		final TransactionView transactionView = (TransactionView) query.getSingleResult();
		final TransactionBean transactionBean = new TransactionBean(transactionView.getId(), transactionView.getType(),
				transactionView.getCreatedDateTime(), transactionView.getAmount());
		return transactionBean;
	}

	@Override
	public List<TransactionBean> getTransactions(final UUID accountId) {
		final Query query = entityManager.createNamedQuery("TransactionView.findAll");
		query.setParameter("accountId", accountId);
		@SuppressWarnings("unchecked")
		final List<TransactionView> transactionViewList = query.getResultList();
		final List<TransactionBean> transactionBeanList = new ArrayList<TransactionBean>();
		for (final TransactionView transactionView : transactionViewList) {
			transactionBeanList.add(new TransactionBean(transactionView.getId(), transactionView.getType(),
					transactionView.getCreatedDateTime(), transactionView.getAmount()));
		}
		return transactionBeanList;
	}
}
