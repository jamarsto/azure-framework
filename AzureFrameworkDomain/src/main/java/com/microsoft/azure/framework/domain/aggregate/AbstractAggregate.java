package com.microsoft.azure.framework.domain.aggregate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.entity.Entity;
import com.microsoft.azure.framework.domain.event.CreatedEvent;
import com.microsoft.azure.framework.domain.event.DeletedEvent;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.SnapshotEvent;
import com.microsoft.azure.framework.precondition.PreconditionService;

public abstract class AbstractAggregate implements Aggregate {
	private static final List<Event> EMPTY = Collections.unmodifiableList(new ArrayList<Event>());
	@Autowired
	private AutowireCapableBeanFactory autowireBeanFactory;
	private Boolean created = Boolean.FALSE;
	private Boolean deleted = Boolean.FALSE;
	private final List<Event> events = new ArrayList<Event>();
	private UUID id;
	private Long lastSnapshot = 0L;
	@Autowired
	protected PreconditionService preconditionService;
	private Long snapshotInterval = 100L;
	private Long version = 0L;

	@Override
	public final Boolean apply(final List<Event> events) {
		preconditionService.requiresNotNull("Events are required.", events);

		Long count = 0L;
		Long offset = 0L;
		try {
			Long localVersion = version;
			for (final Serializable event : events) {
				count++;
				localVersion++;
				if (created && event instanceof CreatedEvent) {
					throw new AlreadyExistsException("Aggregate already exists");
				}
				if(!created && !(event instanceof CreatedEvent)) {
					throw new DoesNotExistException("Aggregate does not exist");
				}
				if (deleted) {
					throw new DoesNotExistException("Aggregate does not exist");
				}
				final Boolean result = (Boolean) this.getClass().getMethod("apply", event.getClass()).invoke(this,
						event);
				if (result.equals(Boolean.FALSE)) {
					return Boolean.FALSE;
				}
				if (event instanceof SnapshotEvent) {
					created = Boolean.TRUE;
					lastSnapshot = localVersion;
					offset = count;
				}
				if (event instanceof DeletedEvent) {
					deleted = Boolean.TRUE;
				}
			}
		} catch (final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
		this.events.addAll(events);
		if (version > 1L && !events.isEmpty()
				&& version - lastSnapshot + events.size() - offset + 1 >= snapshotInterval) {
			this.events.add(snapshot());
		}
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
		preconditionService.requiresNotNull("Events are required.", events);

		try {
			for (final Serializable event : events) {
				final Boolean result = (Boolean) this.getClass().getMethod("apply", event.getClass()).invoke(this,
						event);
				if (result.equals(Boolean.FALSE)) {
					return Boolean.FALSE;
				}
			}
		} catch (final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
		return Boolean.TRUE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<Event> decide(final Command command) {
		preconditionService.requiresNotNull("Command is required.", command);

		try {
			if (deleted) {
				return EMPTY;
			}
			final List<Event> events = (List<Event>) this.getClass().getMethod("decide", command.getClass())
					.invoke(this, command);
			return events;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	@Override
	public final List<Event> getEvents() {
		return Collections.unmodifiableList(events);
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

	protected final void setSnapshotInterval(final Long snapshotInterval) {
		this.snapshotInterval = snapshotInterval;
	}
	
	protected abstract SnapshotEvent snapshot();
}
