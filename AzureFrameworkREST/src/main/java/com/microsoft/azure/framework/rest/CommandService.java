package com.microsoft.azure.framework.rest;

import com.microsoft.azure.framework.command.Command;

public interface CommandService {

	Command createCommand(String commandName, String json);

}
