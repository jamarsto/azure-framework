package com.microsoft.azure.framework.eventbus.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.impl.EventEntry;
import com.microsoft.azure.framework.eventbus.EventBus;
import com.microsoft.azure.framework.eventbus.configuration.EventBusConfiguration;
import com.microsoft.azure.framework.precondition.PreconditionService;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.TopicInfo;

@Component
public final class SimpleEventBus implements EventBus {
	@Autowired
	private PreconditionService preconditionService;
	@Autowired
	private EventBusConfiguration eventBusConfiguration;

	private ServiceBusContract getTopicService(final String serviceName, final String secretName,
			final String topicPath, final TopicInfo topicInfo) {
		final Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(serviceName,
				"RootManageSharedAccessKey", System.getenv(secretName), ".servicebus.windows.net");
		final ServiceBusContract service = ServiceBusService.create(config);
		try {
			service.getTopic(topicPath);
		} catch (final ServiceException | WebApplicationException e) {
			try {
				service.createTopic(topicInfo);
			} catch (final ServiceException | WebApplicationException se) {
				throw new AggregateException(se.getMessage(), se);
			}
		}
		return service;
	}

	@Override
	public void publish(final Aggregate aggregate, final List<Event> events) {
		preconditionService.requiresNotNull("Aggregate is required.", aggregate);
		preconditionService.requiresNotNull("Events are required.", aggregate);

		final String topicPath = aggregate.getClass().getName();
		final TopicInfo topicInfo = new TopicInfo(topicPath);

		try {
			final ServiceBusContract service = getTopicService(eventBusConfiguration.getServiceName(),
					eventBusConfiguration.getSecretName(), topicPath, topicInfo);
			publish(aggregate, events, service, topicPath);
		} catch (final AggregateException e) {
			if (eventBusConfiguration.getBackupServiceName() != null
					&& eventBusConfiguration.getBackupSecretName() != null) {
				if (e.getCause() instanceof ServiceException || e.getCause() instanceof WebApplicationException) {
					final ServiceBusContract service = getTopicService(eventBusConfiguration.getBackupServiceName(),
							eventBusConfiguration.getBackupSecretName(), topicPath, topicInfo);
					publish(aggregate, events, service, topicPath);
					return;
				}
			}
			throw e;
		}
	}

	private void publish(final Aggregate aggregate, final List<Event> events, final ServiceBusContract service,
			final String topicPath) {
		try {
			final List<EventEntry> eventEntries = new ArrayList<EventEntry>();
			for (final Event event : events) {
				eventEntries.add(new EventEntry(event));
			}
			final ObjectMapper mapper = new ObjectMapper();
			final String eventsString = mapper.writeValueAsString(eventEntries);
			final BrokeredMessage message = new BrokeredMessage(eventsString);
			message.setProperty("aggregateClassName", aggregate.getClass().getName());
			message.setProperty("aggregateId", aggregate.getID());
			message.setProperty("fromVersion", aggregate.getVersion() + 1L);
			message.setProperty("toVersion", aggregate.getVersion() + eventEntries.size());
			service.sendTopicMessage(topicPath, message);
		} catch (final ServiceException | WebApplicationException e) {
			throw new AggregateException(e.getMessage(), e);
		} catch (final IOException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}
}
