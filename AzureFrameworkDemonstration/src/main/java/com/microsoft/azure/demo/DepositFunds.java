package com.microsoft.azure.demo;

import java.math.BigDecimal;
import java.util.UUID;

import com.microsoft.azure.framework.command.Command;

public interface DepositFunds extends Command {

	interface Builder extends Command.Builder {

		DepositFunds build();
		
		Builder buildAccountId(UUID id);
		
		Builder buildAmount(BigDecimal amount);
	
	}
	
	interface BuilderFactory extends Command.BuilderFactory {
		
		Builder create();
		
	}
	
}
