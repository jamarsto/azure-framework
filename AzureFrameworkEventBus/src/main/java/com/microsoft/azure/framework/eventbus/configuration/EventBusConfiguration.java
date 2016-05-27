package com.microsoft.azure.framework.eventbus.configuration;

public interface EventBusConfiguration {
	
	String getBackupServiceName();
	
	String getBackupSecretName();

	String getServiceName();
	
	String getSecretName();
}
