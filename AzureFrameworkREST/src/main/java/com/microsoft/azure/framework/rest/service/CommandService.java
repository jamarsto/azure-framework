package com.microsoft.azure.framework.rest.service;

import com.microsoft.azure.framework.command.Command;

public interface CommandService {

	Command createCommand(String commandName, String json);

}
