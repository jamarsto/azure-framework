package com.microsoft.azure.framework.eventbus.impl;

import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.eventbus.EventBusConfiguration;

@Component("eventBusConfiguration")
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
