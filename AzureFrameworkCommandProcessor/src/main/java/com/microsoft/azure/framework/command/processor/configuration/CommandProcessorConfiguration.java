package com.microsoft.azure.framework.command.processor.configuration;

import java.util.Map;

import com.microsoft.azure.framework.domain.service.DomainService;

public interface CommandProcessorConfiguration {

	Map<String, DomainService> getRoutingMap();

	void setRoutingMap(Map<String, DomainService> routingMap);

}
