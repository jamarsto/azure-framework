package com.microsoft.azure.framework.command;

import java.util.UUID;

public interface Command {

	UUID getAggregateId();
	
	void validate();

}
