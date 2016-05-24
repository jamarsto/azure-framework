package com.microsoft.azure.framework.domain.service;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.aggregate.AbstractAggregate;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.eventbus.EventBus;
import com.microsoft.azure.framework.eventstore.InputEventStream;
import com.microsoft.azure.framework.eventstore.OutputEventStream;

public abstract class AbstractDomainService implements DomainService {
	private DomainServiceConfiguration domainServiceConfiguration;
	private AutowireCapableBeanFactory autowireBeanFactory;
	private EventBus eventBus;

	protected final void applyEvents(final String message, final Aggregate aggregate, final List<Event> events) {
		final Boolean result = aggregate.apply(events);
		if (result.equals(Boolean.FALSE)) {
			throw new AggregateException(message);
		}
	}

	protected final Aggregate getEmptyAggregate(final Command command) {
		try {
			final Class<?> clazz = domainServiceConfiguration.getRoutingMap().get(command.getClass().getName());
			final Aggregate aggregate = (Aggregate) clazz.newInstance();
			autowireBeanFactory.autowireBean(aggregate);
			initialize(aggregate, command.getAggregateId());
			return aggregate;
		} catch (InstantiationException | IllegalAccessException | BeansException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	private Long getLatestSnapshotVersion(final Command command, final Aggregate aggregate) {
		final InputEventStream.Builder inputEventStreamBuilder = domainServiceConfiguration
				.getInputEventStreamBuilderFactory().create();
		inputEventStreamBuilder.buildPartitionID(domainServiceConfiguration.getPartitionID())
				.buildBucketID(aggregate.getClass().getName()).buildStreamID(command.getAggregateId());
		final Class<?> clazz = domainServiceConfiguration.getSnapshotMap().get(aggregate.getClass().getName());
		inputEventStreamBuilder.buildFilter(clazz).buildFromVersion(Long.MAX_VALUE);
		try (final InputEventStream ies = inputEventStreamBuilder.build()) {
			if (ies.available() == 0) {
				return 0L;
			}
			return ies.getFromVersion();
		} catch (IOException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected final void populateAggregateFromStream(final Command command, final Aggregate aggregate) {
		final InputEventStream.Builder inputEventStreamBuilder = domainServiceConfiguration
				.getInputEventStreamBuilderFactory().create();
		inputEventStreamBuilder.buildPartitionID(domainServiceConfiguration.getPartitionID())
				.buildBucketID(aggregate.getClass().getName()).buildStreamID(command.getAggregateId())
				.buildFilter(Serializable.class).buildFromVersion(getLatestSnapshotVersion(command, aggregate))
				.buildToVersion(Long.MAX_VALUE);
		try (final InputEventStream ies = inputEventStreamBuilder.build()) {
			if (ies.available() > 0) {
				initialize(aggregate, ies.getFromVersion() - 1L);
				applyEvents("Aggregate failed to apply events from the event store.", aggregate,
						(List<Event>) (List<?>) ies.readAll());
				aggregate.commit();
			}
		} catch (IOException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	private void initialize(final Aggregate aggregate, final UUID id) {
		try {
			final Method method = AbstractAggregate.class.getDeclaredMethod("initialize", UUID.class, Long.class);
			method.setAccessible(true);
			method.invoke(aggregate, id, 0L);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	private void initialize(final Aggregate aggregate, final Long fromVersion) {
		try {
			final Method method = AbstractAggregate.class.getDeclaredMethod("initialize", UUID.class, Long.class);
			method.setAccessible(true);
			method.invoke(aggregate, aggregate.getID(), fromVersion);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected final void populateStreamFromAggregate(final Aggregate aggregate) {
		final OutputEventStream.Builder outputEventStreamBuilder = domainServiceConfiguration
				.getOutputEventStreamBuilderFactory().create();
		outputEventStreamBuilder.buildPartitionID(domainServiceConfiguration.getPartitionID())
				.buildBucketID(aggregate.getClass().getName()).buildStreamID(aggregate.getID())
				.buildFromVersion(aggregate.getVersion() + 1L);
		try (final OutputEventStream oes = outputEventStreamBuilder.build()) {
			oes.write((List<Serializable>) (List<?>) aggregate.getEvents());
			oes.flush(UUID.randomUUID());
		} catch (IOException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}
	
	protected final void publishEvents(final Aggregate aggregate, final List<Event> events) {
		eventBus.publish(aggregate, events);
	}

	@Autowired
	public final void setDomainServiceConfiguration(final DomainServiceConfiguration domainServiceConfiguration) {
		this.domainServiceConfiguration = domainServiceConfiguration;
	}

	@Autowired
	public final void setAutowireCapableBeanFactory(final AutowireCapableBeanFactory autowireBeanFactory) {
		this.autowireBeanFactory = autowireBeanFactory;
	}
	
	@Autowired
	public final void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}
}
