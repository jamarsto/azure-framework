package com.microsoft.azure.framework.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.service.AbstractDomainService;
import com.microsoft.azure.framework.domain.service.DomainServiceException;
import com.microsoft.azure.framework.precondition.PreconditionService;

@Component(value = "defaultDomainService")
public final class SimpleDomainService extends AbstractDomainService {
	@Autowired
	private PreconditionService preconditionService;

	@Override
	public void doCommand(final Command command) {
		preconditionService.requiresNotNull("Command is required.", command);
		
		final Aggregate aggregate = getEmptyAggregate(command);
		populateAggregateFromStream(command, aggregate);
		final List<Event> events = aggregate.decide(command);
		if (!events.isEmpty()) {
			applyEvents("Aggregate failed to apply events.", aggregate, events);
		} else {
			throw new DomainServiceException("Aggregate refused to apply events.");
		}
		populateStreamFromAggregate(aggregate);
		publishEvents(aggregate);
		aggregate.commit();
	}
}
