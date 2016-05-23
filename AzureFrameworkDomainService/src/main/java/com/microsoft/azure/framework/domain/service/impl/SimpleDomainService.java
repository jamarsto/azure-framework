package com.microsoft.azure.framework.domain.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.service.AbstractDomainService;
import com.microsoft.azure.framework.domain.service.DomainServiceException;

@Component(value = "defaultDomainService")
public final class SimpleDomainService extends AbstractDomainService {

	@Override
	public void doCommand(final Command command) {
		final Aggregate aggregate = getEmptyAggregate(command);
		populateAggregateFromStream(command, aggregate);
		final List<Event> events = aggregate.decide(command);
		if (!events.isEmpty()) {
			applyEvents("Aggregate failed to apply events.", aggregate, events);
		} else {
			throw new DomainServiceException("Aggregate refused to apply events.");
		}
		populateStreamFromAggregate(aggregate);
		// publish events here
		aggregate.commit();
	}
}
