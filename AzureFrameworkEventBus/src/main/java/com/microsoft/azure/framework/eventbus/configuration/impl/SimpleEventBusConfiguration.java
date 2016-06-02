package com.microsoft.azure.framework.eventbus.configuration.impl;

import java.util.List;

import com.microsoft.azure.framework.eventbus.configuration.EventBusConfiguration;
import com.microsoft.azure.framework.eventbus.configuration.Namespace;

public final class SimpleEventBusConfiguration implements EventBusConfiguration {
	private List<Namespace> namespaces;
	private String partitionID;

	@Override
	public List<Namespace> getNamespaces() {
		return namespaces;
	}

	@Override
	public String getPartitionID() {
		return partitionID;
	}

	public void setNamespaces(final List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}

	public void setPartitionID(final String partitionID) {
		this.partitionID = partitionID;
	}
}
