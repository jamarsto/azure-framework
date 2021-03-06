package com.microsoft.azure.framework.eventbus.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.impl.EventEntry;
import com.microsoft.azure.framework.eventbus.EventBus;
import com.microsoft.azure.framework.precondition.PreconditionService;
import com.microsoft.azure.framework.servicebus.configuration.AzureServiceBusConfiguration;
import com.microsoft.azure.framework.servicebus.configuration.Namespace;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.TopicInfo;

@Component
public final class SimpleEventBus implements EventBus {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventBus.class);
	@Autowired
	private PreconditionService preconditionService;
	@Autowired
	private AzureServiceBusConfiguration azureServiceBusConfiguration;

	private ServiceBusContract getTopicService(final String serviceName, final String secretName,
			final String topicPath) {
		final Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(serviceName,
				"RootManageSharedAccessKey", secretName, ".servicebus.windows.net");
		final ServiceBusContract service = ServiceBusService.create(config);
		try {
			service.getTopic(topicPath);
		} catch (final ServiceException | WebApplicationException e) {
			try {
				service.createTopic(new TopicInfo(topicPath));
			} catch (final ServiceException | WebApplicationException se) {
				throw new AggregateException(se.getMessage(), se);
			}
		}
		return service;
	}

	@Override
	public void publish(final Aggregate aggregate) {
		preconditionService.requiresNotNull("Aggregate is required.", aggregate);
		if (aggregate.getEvents() != null && !aggregate.getEvents().isEmpty()) {
			publish(aggregate.getClass().getName(), aggregate.getID(), aggregate.getVersion(), aggregate.getEvents());
		}
	}

	private void publish(final String bucketID, final UUID streamID, final Long version, final List<Event> events) {
		final List<Namespace> namespaces = azureServiceBusConfiguration.getNamespaces();
		AggregateException lastException = null;
		for (final Namespace namespace : namespaces) {
			try {
				final ServiceBusContract service = getTopicService(namespace.getName(), namespace.getSecret(),
						bucketID);
				publish(bucketID, streamID, version, events, service);
				return;
			} catch (final AggregateException e) {
				lastException = e;
				if (e.getCause() instanceof ServiceException || e.getCause() instanceof WebApplicationException) {
					continue;
				}
				throw e;
			}
		}
		LOGGER.error(String.format("Unable to publish Event %s : %s : %s", bucketID, streamID, version));
		throw lastException;
	}

	private void publish(final String bucketID, final UUID streamID, final Long version,
			final List<Event> events, final ServiceBusContract service) {
		try {
			final List<EventEntry> eventEntries = new ArrayList<EventEntry>();
			final ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			for (final Event event : events) {
				eventEntries.add(new EventEntry(event.getClass().getName(), mapper.writeValueAsString(event)));
			}
			final String eventsString = mapper.writeValueAsString(eventEntries);
			final BrokeredMessage message = new BrokeredMessage(eventsString);
			message.setProperty("aggregateClassName", bucketID);
			message.setProperty("aggregateId", streamID.toString());
			final Long fromVersion = version + 1L;
			message.setProperty("fromVersion", fromVersion);
			final Long toVersion = version + eventEntries.size();
			message.setProperty("toVersion", toVersion);
			service.sendTopicMessage(bucketID, message);
		} catch (final ServiceException | WebApplicationException e) {
			throw new AggregateException(e.getMessage(), e);
		} catch (final IOException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}
}
