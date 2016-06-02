package com.microsoft.azure.framework.eventbus.configuration;

import java.util.List;

public interface EventBusConfiguration {
	
	String getPartitionID();

	List<Namespace> getNamespaces();
}
