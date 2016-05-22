package com.microsoft.azure.framework.domain.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.service.AbstractDomainService;
import com.microsoft.azure.framework.domain.service.DomainServiceException;

@Component(value="defaultDomainService")
public final class SimpleDomainService extends AbstractDomainService {

	@Override
	public void doCommand(final Command command) {
		final Aggregate entity = getEmptyAggregate(command);
		populateAggregateFromStream(command, entity);
		final List<Event> events = entity.decide(command);
		if (!events.isEmpty()) {
			applyEvents("Entity failed to apply events.", entity, events);
		} else {
			throw new DomainServiceException("Entity refused to apply events.");
		}
		populateStreamFromAggregate(entity, events);
		// publish events here
		entity.commit();
	}
}
