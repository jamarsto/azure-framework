package com.microsoft.azure.framework.servicebus.configuration.impl;

import java.util.List;

import com.microsoft.azure.framework.servicebus.configuration.AzureServiceBusConfiguration;
import com.microsoft.azure.framework.servicebus.configuration.Namespace;

public final class SimpleAzureServiceBusConfiguration implements AzureServiceBusConfiguration {
	private List<Namespace> namespaces;
	private Integer numberOfReceivers = 1;
	private Integer receiverTimeout = 1;

	@Override
	public List<Namespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(final List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}

	@Override
	public Integer getNumberOfReceivers() {
		return numberOfReceivers;
	}

	@Override
	public Integer getReceiverTimout() {
		return receiverTimeout;
	}
	
	public void setNumberOfReceivers(final Integer numberOfReceivers) {
		if (numberOfReceivers != null && numberOfReceivers > 1) {
			if (numberOfReceivers > 50) {
				this.numberOfReceivers = 50;
			} else {
				this.numberOfReceivers = numberOfReceivers;
			}
		}
	}
	
	public void setReceiverTimeout(final Integer receiverTimeout) {
		if (receiverTimeout != null && receiverTimeout > 1) {
			if (receiverTimeout > 60) {
				this.receiverTimeout = 60;
			} else {
				this.receiverTimeout = receiverTimeout;
			}
		}
	}
}
