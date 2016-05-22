package com.microsoft.azure.framework.domain.service;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.aggregate.AbstractAggregate;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.eventstore.InputEventStream;
import com.microsoft.azure.framework.eventstore.OutputEventStream;

public abstract class AbstractDomainService implements DomainService {
	private DomainServiceConfiguration domainServiceConfiguration;
	private AutowireCapableBeanFactory autowireBeanFactory;

	protected final void applyEvents(final String message, final Aggregate aggregate, final List<Event> events) {
		final Boolean result = aggregate.apply(events);
		if (result.equals(Boolean.FALSE)) {
			throw new DomainServiceException(message);
		}
	}

	protected final Aggregate getEmptyAggregate(final Command command) {
		try {
			final Class<?> clazz = domainServiceConfiguration.getRoutingMap().get(command.getClass().getName());
			final Aggregate aggregate = (Aggregate) clazz.newInstance();
			autowireBeanFactory.autowireBean(aggregate);
			return aggregate;
		} catch (InstantiationException | IllegalAccessException | BeansException e) {
			throw new DomainServiceException(e.getMessage(), e);
		}
	}

	private Long getLatestSnapshotVersion(final Command command, final Aggregate aggregate) {
		final InputEventStream.Builder inputEventStreamBuilder = domainServiceConfiguration
				.getInputEventStreamBuilderFactory().create();
		inputEventStreamBuilder.buildPartitionID(domainServiceConfiguration.getPartitionID())
				.buildBucketID(aggregate.getClass().getName()).buildStreamID(command.getAggregateID())
				.buildFilter(Serializable.class);
		final Class<?> clazz = domainServiceConfiguration.getSnapshotMap().get(aggregate.getClass().getName());
		inputEventStreamBuilder.buildFilter(clazz).buildFromVersion(Long.MAX_VALUE);
		try (final InputEventStream ies = inputEventStreamBuilder.build()) {
			if (ies.available() == 0) {
				return 0L;
			}
			return ies.getFromVersion();
		} catch (IOException e) {
			throw new DomainServiceException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected final void populateAggregateFromStream(final Command command, final Aggregate aggregate) {
		final InputEventStream.Builder inputEventStreamBuilder = domainServiceConfiguration
				.getInputEventStreamBuilderFactory().create();
		inputEventStreamBuilder.buildPartitionID(domainServiceConfiguration.getPartitionID())
				.buildBucketID(aggregate.getClass().getName()).buildStreamID(command.getAggregateID())
				.buildFilter(Serializable.class).buildFromVersion(getLatestSnapshotVersion(command, aggregate))
				.buildToVersion(Long.MAX_VALUE);
		try (final InputEventStream ies = inputEventStreamBuilder.build()) {
			if (ies.available() > 0) {
				initializeVersion(aggregate, ies);
				applyEvents("Aggregate failed to apply events from the event store.", aggregate,
						(List<Event>) (List<?>) ies.readAll());
				aggregate.commit();
			}
		} catch (IOException e) {
			throw new DomainServiceException(e.getMessage(), e);
		}
	}

	private void initializeVersion(final Aggregate aggregate, final InputEventStream ies) {
		try {
			final Method method = AbstractAggregate.class.getDeclaredMethod("initializeVersion", Long.class);
			method.setAccessible(true);
			method.invoke(aggregate, ies.getFromVersion() - 1L);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new DomainServiceException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected final void populateStreamFromAggregate(final Aggregate aggregate, final List<Event> events) {
		final OutputEventStream.Builder outputEventStreamBuilder = domainServiceConfiguration
				.getOutputEventStreamBuilderFactory().create();
		outputEventStreamBuilder.buildPartitionID(domainServiceConfiguration.getPartitionID())
				.buildBucketID(aggregate.getClass().getName()).buildStreamID(aggregate.getID())
				.buildFromVersion(aggregate.getVersion() + 1L);
		try (final OutputEventStream oes = outputEventStreamBuilder.build()) {
			oes.write((List<Serializable>) (List<?>) events);
		} catch (IOException e) {
			throw new DomainServiceException(e.getMessage(), e);
		}
	}

	@Autowired
	public final void setDomainServiceConfiguration(final DomainServiceConfiguration domainServiceConfiguration) {
		this.domainServiceConfiguration = domainServiceConfiguration;
	}

	@Autowired
	public final void setAutowireCapableBeanFactory(final AutowireCapableBeanFactory autowireBeanFactory) {
		this.autowireBeanFactory = autowireBeanFactory;
	}

}
