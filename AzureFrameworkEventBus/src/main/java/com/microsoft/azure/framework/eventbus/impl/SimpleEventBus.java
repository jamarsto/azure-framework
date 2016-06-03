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
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.impl.EventEntry;
import com.microsoft.azure.framework.eventbus.EventBus;
import com.microsoft.azure.framework.eventbus.configuration.EventBusConfiguration;
import com.microsoft.azure.framework.eventbus.configuration.Namespace;
import com.microsoft.azure.framework.precondition.PreconditionService;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveSubscriptionMessageResult;
import com.microsoft.windowsazure.services.servicebus.models.SubscriptionInfo;
import com.microsoft.windowsazure.services.servicebus.models.TopicInfo;

@Component
public final class SimpleEventBus implements EventBus {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventBus.class);
	@Autowired
	private PreconditionService preconditionService;
	@Autowired
	private EventBusConfiguration eventBusConfiguration;

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
		final String partitionID = eventBusConfiguration.getPartitionID();
		final List<Namespace> namespaces = eventBusConfiguration.getNamespaces();
		AggregateException lastException = null;
		for (final Namespace namespace : namespaces) {
			try {
				final ServiceBusContract service = getTopicService(namespace.getName(), namespace.getSecret(),
						bucketID);
				publish(partitionID, bucketID, streamID, version, events, service);
				return;
			} catch (final AggregateException e) {
				lastException = e;
				if (e.getCause() instanceof ServiceException || e.getCause() instanceof WebApplicationException) {
					continue;
				}
				throw e;
			}
		}
		LOGGER.error(String.format("Unable to publish Event %s : %s : %s : %s", eventBusConfiguration.getPartitionID(),
				bucketID, streamID, version));
		throw lastException;
	}

	private void publish(final String partitionID, final String bucketID, final UUID streamID, final Long version,
			final List<Event> events, final ServiceBusContract service) {
		try {
			final List<EventEntry> eventEntries = new ArrayList<EventEntry>();
			final ObjectMapper mapper = new ObjectMapper();
			for (final Event event : events) {
				eventEntries.add(new EventEntry(event.getClass().getName(), mapper.writeValueAsString(event)));
			}
			final String eventsString = mapper.writeValueAsString(eventEntries);
			final BrokeredMessage message = new BrokeredMessage(eventsString);
			// message.setProperty("isCatchUp", Boolean.FALSE);
			message.setProperty("partitionID", partitionID);
			message.setProperty("aggregateClassName", bucketID);
			message.setProperty("aggregateId", streamID);
			message.setProperty("fromVersion", version + 1L);
			message.setProperty("toVersion", version + eventEntries.size());
			service.sendTopicMessage(bucketID, message);
		} catch (final ServiceException | WebApplicationException e) {
			throw new AggregateException(e.getMessage(), e);
		} catch (final IOException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}

	private void consume(final String serviceName, final String secretName, final String topicPath,
			final String viewName) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final ServiceBusContract service = getSubscriptionService(serviceName, secretName, topicPath, viewName);
			final ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
			opts.setReceiveMode(ReceiveMode.PEEK_LOCK);
			opts.setTimeout(60);
			while (!Thread.interrupted()) {
				final ReceiveSubscriptionMessageResult result = service.receiveSubscriptionMessage(topicPath, viewName,
						opts);
				if (result == null || Thread.interrupted()) {
					continue;
				}
				final BrokeredMessage message = result.getValue();
				final String partitionID = (String) message.getProperty("partitionID");
				final String aggregateClassName = (String) message.getProperty("aggregateClassName");
				final String aggregateID = (String) message.getProperty("aggregateID");
				final Long fromVersion = (Long) message.getProperty("fromVersion");
				final Long toVersion = (Long) message.getProperty("toVersion");
				final EventEntry[] eventEntryArray = mapper.readValue(message.getBody(), EventEntry[].class);
				final List<Event> events = new ArrayList<Event>();
				for (final EventEntry eventEntry : eventEntryArray) {
					@SuppressWarnings("unchecked")
					final Class<? extends Event> clazz = (Class<? extends Event>) Class
							.forName(eventEntry.getEventClassName());
					final Event event = mapper.readValue(eventEntry.getEvent(), clazz);
					events.add(event);
				}
			}
		} catch (final ServiceException | WebApplicationException e) {
			e.printStackTrace();
		} catch (final IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private ServiceBusContract getSubscriptionService(final String serviceName, final String secretName,
			final String topicPath, final String viewName) {
		final Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(serviceName,
				"RootManageSharedAccessKey", secretName, ".servicebus.windows.net");
		final ServiceBusContract service = ServiceBusService.create(config);
		final SubscriptionInfo subscriptionInfo = new SubscriptionInfo(viewName);
		try {
			service.getSubscription(topicPath, viewName);
		} catch (final ServiceException | WebApplicationException e) {
			try {
				service.createSubscription(topicPath, subscriptionInfo);
			} catch (final ServiceException | WebApplicationException se) {
				throw new AggregateException(se.getMessage(), se);
			}
		}
		return service;
	}
	//
	// private void publishViewCatchUp(final String partitionID, final String
	// bucketID, final ServiceBusContract service) {
	// try {
	// final BrokeredMessage message = new BrokeredMessage("REBUILD");
	// message.setProperty("isCatchUp", Boolean.TRUE);
	// message.setProperty("partitionID", partitionID);
	// service.sendTopicMessage(bucketID, message);
	// } catch (final ServiceException | WebApplicationException e) {
	// throw new AggregateException(e.getMessage(), e);
	// }
	// }
}
