package com.microsoft.azure.framework.domain.service.configuration;

import java.util.Map;

import com.microsoft.azure.framework.eventstore.InputEventStream;
import com.microsoft.azure.framework.eventstore.OutputEventStream;

public interface DomainServiceConfiguration {

	public InputEventStream.BuilderFactory getInputEventStreamBuilderFactory();

	public OutputEventStream.BuilderFactory getOutputEventStreamBuilderFactory();

	public Map<String, Class<?>> getRoutingMap();

	public Map<String, Class<?>> getSnapshotMap();

	public void setInputEventStreamBuilderFactory(InputEventStream.BuilderFactory inputEventStreamBuilderFactory);

	public void setOutputEventStreamBuilderFactory(OutputEventStream.BuilderFactory outputEventStreamBuilderFactory);

	public void setRoutingMap(Map<String, Class<?>> routingMap);

	public void setSnapshotMap(Map<String, Class<?>> snapshotMap);

}
