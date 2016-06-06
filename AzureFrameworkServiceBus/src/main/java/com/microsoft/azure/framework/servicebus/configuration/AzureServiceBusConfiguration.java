package com.microsoft.azure.framework.servicebus.configuration;

import java.util.List;

public interface AzureServiceBusConfiguration {

	List<Namespace> getNamespaces();
	
	Integer getNumberOfReceivers();
	
	Integer getReceiverTimout();
}
