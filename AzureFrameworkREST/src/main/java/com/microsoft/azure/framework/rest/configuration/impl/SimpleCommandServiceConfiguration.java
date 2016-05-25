package com.microsoft.azure.framework.rest.configuration.impl;

import java.util.Map;

import com.microsoft.azure.framework.rest.configuration.CommandServiceConfiguration;

public final class SimpleCommandServiceConfiguration implements CommandServiceConfiguration {
	private Map<String, Class<?>> commandMap;

	@Override
	public Map<String, Class<?>> getCommandMap() {
		return commandMap;
	}

	public void setCommandMap(final Map<String, Class<?>> commandMap) {
		this.commandMap = commandMap;
	}
}
