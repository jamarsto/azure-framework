package com.microsoft.azure.framework.domain.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.precondition.PreconditionService;

public abstract class AbstractEntity implements Entity {
	@Autowired
	private AutowireCapableBeanFactory autowireBeanFactory;
	@Autowired
	protected PreconditionService preconditionService;

	@Override
	public final Boolean apply(final List<Event> events) {
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
			throw new EntityException(e.getMessage(), e);
		}
		return Boolean.TRUE;
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
			throw new EntityException(e.getMessage(), e);
		}
		return Boolean.TRUE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<Event> decide(final Command command) {
		try {
			final List<Event> events = (List<Event>) this.getClass().getMethod("decide", command.getClass())
					.invoke(this, command);
			return events;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new EntityException(e.getMessage(), e);
		}
	}

	protected final <T extends Entity> T inject(T object) {
		autowireBeanFactory.autowireBean(object);
		return object;
	}
}
