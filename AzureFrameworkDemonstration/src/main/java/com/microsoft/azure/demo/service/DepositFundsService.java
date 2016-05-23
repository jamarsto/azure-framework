package com.microsoft.azure.demo.service;

import java.math.BigDecimal;

public interface DepositFundsService {

	void depositFunds();

	void setAmount(final BigDecimal amount);

	BigDecimal getAmount();

}
