package com.microsoft.azure.framework.eventbus.configuration.impl;

import com.microsoft.azure.framework.eventbus.configuration.EventBusConfiguration;

public final class SimpleEventBusConfiguration implements EventBusConfiguration {
	private String backupSecretName;
	private String backupServiceName;
	private String secretName;
	private String serviceName;

	@Override
	public String getBackupSecretName() {
		return backupSecretName;
	}

	@Override
	public String getBackupServiceName() {
		return backupServiceName;
	}

	@Override
	public String getSecretName() {
		return secretName;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	public void setBackupSecretName(final String backupSecretName) {
		this.backupSecretName = backupSecretName;
	}

	public void setBackupServiceName(final String backupServiceName) {
		this.backupServiceName = backupServiceName;
	}

	public void setSecretName(final String secretName) {
		this.secretName = secretName;
	}

	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}
}
