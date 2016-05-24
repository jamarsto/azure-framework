package com.microsoft.azure.framework.command.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.command.processor.CommandProcessor;
import com.microsoft.azure.framework.command.processor.configuration.CommandProcessorConfiguration;
import com.microsoft.azure.framework.domain.service.DomainService;
import com.microsoft.azure.framework.precondition.PreconditionService;

@Component
public final class SimpleCommandProcessor implements CommandProcessor {
	@Autowired
	private PreconditionService preconditionService;
	@Autowired
	private CommandProcessorConfiguration commandProcessorConfiguration;

	@Override
	public void doCommand(final Command command) {
		preconditionService.requiresNotNull("Command is required.", command);

		DomainService service = commandProcessorConfiguration.getRoutingMap().get(command.getClass().getName());
		if (service == null) {
			service = commandProcessorConfiguration.getRoutingMap().get("DEFAULT_DOMAIN_SERVICE");
		}
		service.doCommand(command);
	}
}
