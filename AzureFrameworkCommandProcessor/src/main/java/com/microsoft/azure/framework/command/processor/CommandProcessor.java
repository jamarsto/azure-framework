package com.microsoft.azure.framework.command.processor;

import com.microsoft.azure.framework.command.Command;

public interface CommandProcessor {
	
	void doCommand(Command command);
	
}
