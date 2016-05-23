package com.microsoft.azure.demo.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.demo.CommandService;
import com.microsoft.azure.demo.CommandServiceConfiguration;
import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.command.CommandException;

@Component
public class SimpleCommandService implements CommandService {
	@Autowired
	private CommandServiceConfiguration commandServiceConfiguration;
	
	@Override
	public Command createCommand(final String commandName, final String json) {
		final Class<?> clazz = commandServiceConfiguration.getCommandMap().get(commandName);
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return (Command) mapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}
}
