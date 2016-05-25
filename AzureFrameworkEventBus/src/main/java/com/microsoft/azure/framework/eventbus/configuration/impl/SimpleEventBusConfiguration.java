package com.microsoft.azure.framework.eventbus.configuration.impl;

import com.microsoft.azure.framework.eventbus.configuration.EventBusConfiguration;

public final class SimpleEventBusConfiguration implements EventBusConfiguration {
	private String serviceName;
	private String secretName;

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String getSecretName() {
		return secretName;
	}

	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

	public void setSecretName(final String secretName) {
		this.secretName = secretName;
	}
}
