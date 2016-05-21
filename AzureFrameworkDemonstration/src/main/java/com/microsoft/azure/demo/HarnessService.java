package com.microsoft.azure.demo;

import java.math.BigDecimal;

public interface HarnessService {

	void depositFunds(BigDecimal amount);
	
	void testPreconditions();

}
