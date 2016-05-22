package com.microsoft.azure.framework.domain.aggregate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.entity.Entity;
import com.microsoft.azure.framework.domain.event.CreatedEvent;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.SnapshotEvent;
import com.microsoft.azure.framework.precondition.PreconditionService;

public abstract class AbstractAggregate implements Aggregate {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAggregate.class);
	@Autowired
	private AutowireCapableBeanFactory autowireBeanFactory;
	private final List<Event> events = new ArrayList<Event>();
	private UUID id;
	private Long lastSnapshot = 0L;
	@Autowired
	protected PreconditionService preconditionService;
	private Long snapshotInterval = 10L;
	private Long version = 0L;

	@Override
	public final Boolean apply(final List<Event> events) {
		try {
			if (lastSnapshot == 0L && !events.isEmpty() && !(events.get(0) instanceof SnapshotEvent)) {
				LOGGER.warn("lastSnapshot is zero and the first event is not a SnaphotEvent");
				return Boolean.FALSE;
			}
			Long localVersion = version;
			for (final Serializable event : events) {
				localVersion++;
				final Boolean result = (Boolean) this.getClass().getMethod("apply", event.getClass()).invoke(this,
						event);
				if (result.equals(Boolean.FALSE)) {
					return Boolean.FALSE;
				}
				if (event instanceof SnapshotEvent) {
					lastSnapshot = localVersion;
				}
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
		this.events.addAll(events);
		return Boolean.TRUE;
	}

	@Override
	public final void commit() {
		if (lastSnapshot == 0L && !events.isEmpty() && events.get(0) instanceof CreatedEvent) {
			lastSnapshot = 1L;
		}
		version += events.size();
		events.clear();
	}

	@Override
	public final Boolean compensate(final List<Event> events) {
		try {
			for (final Serializable event : events) {
				final Boolean result = (Boolean) this.getClass().getMethod("apply", event.getClass()).invoke(this,
						event);
				if (result.equals(Boolean.FALSE)) {
					return Boolean.FALSE;
				}
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
		return Boolean.TRUE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<Event> decide(final Command command) {
		try {
			final List<Event> events = (List<Event>) this.getClass().getMethod("decide", command.getClass())
					.invoke(this, command);
			if (!events.isEmpty() && version - lastSnapshot + events.size() + 1 >= snapshotInterval) {
				events.add(snapshot());
			}
			return events;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	@Override
	public final UUID getID() {
		return id;
	}

	protected final Long getLastSnapshot() {
		return lastSnapshot;
	}

	@Override
	public final Long getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private void initialize(final UUID id, final Long version) {
		this.id = id;
		this.version = version;
		this.lastSnapshot = version;
	}

	protected final <T extends Entity> T inject(T object) {
		autowireBeanFactory.autowireBean(object);
		return object;
	}

	@Override
	public final void rollback() {
		Collections.reverse(events);
		final Boolean result = compensate(events);
		if (result.equals(Boolean.FALSE)) {
			throw new AggregateException();
		}
		events.clear();
	}

	protected abstract SnapshotEvent snapshot();
}
