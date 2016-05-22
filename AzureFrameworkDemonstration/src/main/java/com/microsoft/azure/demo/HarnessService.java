package com.microsoft.azure.demo;

import java.math.BigDecimal;

public interface HarnessService {

	void depositFunds();

	void setAmount(final BigDecimal amount);

	BigDecimal getAmount();

}
