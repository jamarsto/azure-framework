package com.microsoft.azure.framework.domain.service;

import com.microsoft.azure.framework.command.Command;

public interface DomainService {
	
	void doCommand(Command command);

}
