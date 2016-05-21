package com.microsoft.azure.demo.internal;

import java.math.BigDecimal;

import com.microsoft.azure.demo.DepositFunds;

public interface InternalDepositFunds extends DepositFunds {

	BigDecimal getAmount();

}
