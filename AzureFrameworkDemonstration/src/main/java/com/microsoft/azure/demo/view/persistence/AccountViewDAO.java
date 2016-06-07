package com.microsoft.azure.demo.view.persistence;

import java.util.List;
import java.util.UUID;

import com.microsoft.azure.demo.view.bean.AccountBean;

public interface AccountViewDAO {

	AccountBean getAccount(UUID accountId);
	
	List<AccountBean> getAccounts();
	
}
