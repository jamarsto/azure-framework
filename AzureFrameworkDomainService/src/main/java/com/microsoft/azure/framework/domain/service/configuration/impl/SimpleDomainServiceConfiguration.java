package com.microsoft.azure.framework.domain.service.configuration.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.azure.framework.domain.service.configuration.DomainServiceConfiguration;
import com.microsoft.azure.framework.eventstore.InputEventStream;
import com.microsoft.azure.framework.eventstore.OutputEventStream;

public final class SimpleDomainServiceConfiguration implements DomainServiceConfiguration {
	@Autowired
	private InputEventStream.BuilderFactory inputEventStreamBuilderFactory;
	@Autowired
	private OutputEventStream.BuilderFactory outputEventStreamBuilderFactory;
	private String partitionID;
	private Map<String, Class<?>> routingMap;
	private Map<String, Class<?>> snapshotMap;

	@Override
	public InputEventStream.BuilderFactory getInputEventStreamBuilderFactory() {
		return inputEventStreamBuilderFactory;
	}

	@Override
	public OutputEventStream.BuilderFactory getOutputEventStreamBuilderFactory() {
		return outputEventStreamBuilderFactory;
	}

	@Override
	public String getPartitionID() {
		return partitionID;
	}

	@Override
	public Map<String, Class<?>> getRoutingMap() {
		return routingMap;
	}

	@Override
	public Map<String, Class<?>> getSnapshotMap() {
		return snapshotMap;
	}

	@Override
	public void setInputEventStreamBuilderFactory(
			final InputEventStream.BuilderFactory inputEventStreamBuilderFactory) {
		this.inputEventStreamBuilderFactory = inputEventStreamBuilderFactory;
	}

	@Override
	public void setOutputEventStreamBuilderFactory(
			final OutputEventStream.BuilderFactory outputEventStreamBuilderFactory) {
		this.outputEventStreamBuilderFactory = outputEventStreamBuilderFactory;
	}

	@Override
	public void setPartitionID(final String partitionID) {
		this.partitionID = partitionID;
	}

	@Override
	public void setRoutingMap(final Map<String, Class<?>> routingMap) {
		this.routingMap = routingMap;
	}

	@Override
	public void setSnapshotMap(final Map<String, Class<?>> snapshotMap) {
		this.snapshotMap = snapshotMap;
	}
}
