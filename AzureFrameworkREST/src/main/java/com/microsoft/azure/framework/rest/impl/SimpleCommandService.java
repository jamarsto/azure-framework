package com.microsoft.azure.framework.rest.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.command.CommandException;
import com.microsoft.azure.framework.precondition.PreconditionService;
import com.microsoft.azure.framework.rest.CommandService;
import com.microsoft.azure.framework.rest.configuration.CommandServiceConfiguration;

@Component
public class SimpleCommandService implements CommandService {
	@Autowired
	private PreconditionService preconditionService;
	@Autowired
	private CommandServiceConfiguration commandServiceConfiguration;
	@Autowired
	private AutowireCapableBeanFactory autowireBeanFactory;

	@Override
	public Command createCommand(final String commandName, final String json) {
		preconditionService.requiresNotEmpty("Command Name is required.", commandName);
		preconditionService.requiresNotEmpty("JSON Object is required.", json);

		final Class<?> clazz = commandServiceConfiguration.getCommandMap().get(commandName);
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final Command command = (Command) mapper.readValue(json, clazz);
			autowireBeanFactory.autowireBean(command);
			command.validate();
			return command;
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}
}
