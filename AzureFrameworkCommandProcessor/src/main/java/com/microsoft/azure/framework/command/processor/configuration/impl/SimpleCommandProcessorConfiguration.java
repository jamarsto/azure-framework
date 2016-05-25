package com.microsoft.azure.framework.command.processor.configuration.impl;

import java.util.Map;

import com.microsoft.azure.framework.command.processor.configuration.CommandProcessorConfiguration;
import com.microsoft.azure.framework.domain.service.DomainService;

public class SimpleCommandProcessorConfiguration implements CommandProcessorConfiguration {
	private Map<String, DomainService> routingMap;

	@Override
	public Map<String, DomainService> getRoutingMap() {
		return routingMap;
	}

	@Override
	public void setRoutingMap(Map<String, DomainService> routingMap) {
		this.routingMap = routingMap;
	}
}
