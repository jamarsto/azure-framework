package com.microsoft.azure.demo;

import java.io.Serializable;
import java.math.BigDecimal;

public interface HarnessService extends Serializable {

	void depositFunds();

	void setAmount(final BigDecimal amount);

	BigDecimal getAmount();

}
