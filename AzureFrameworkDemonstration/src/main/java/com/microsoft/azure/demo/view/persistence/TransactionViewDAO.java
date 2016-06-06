package com.microsoft.azure.demo.view.persistence;

import java.util.List;
import java.util.UUID;

import com.microsoft.azure.demo.view.impl.TransactionBean;

public interface TransactionViewDAO {

	TransactionBean getTransaction(UUID accountId, UUID transactionId);
	
	List<TransactionBean> getTransactions(UUID accountId);
	
}
