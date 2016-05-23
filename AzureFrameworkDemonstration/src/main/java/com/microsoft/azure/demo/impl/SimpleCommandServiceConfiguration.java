package com.microsoft.azure.demo.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.CommandServiceConfiguration;

@Component
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
