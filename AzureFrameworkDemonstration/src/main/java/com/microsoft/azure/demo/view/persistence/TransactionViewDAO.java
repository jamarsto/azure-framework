package com.microsoft.azure.demo.view.persistence;

import java.util.List;
import java.util.UUID;

import com.microsoft.azure.demo.view.bean.TransactionBean;

public interface TransactionViewDAO {

	TransactionBean getTransaction(UUID accountId, UUID transactionId);
	
	List<TransactionBean> getTransactions(UUID accountId);
	
}
