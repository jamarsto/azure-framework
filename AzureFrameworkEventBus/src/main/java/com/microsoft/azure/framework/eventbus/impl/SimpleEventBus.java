package com.microsoft.azure.framework.eventbus.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.eventbus.EventBus;
import com.microsoft.azure.framework.eventbus.EventBusConfiguration;
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
	private EventBusConfiguration eventBusConfiguration;

	@Override
	public void publish(final Aggregate aggregate, final List<Event> events) {
		final Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(
				eventBusConfiguration.getServiceName(), "RootManageSharedAccessKey",
				System.getenv(eventBusConfiguration.getSecretName()), ".servicebus.windows.net");
		final ServiceBusContract service = ServiceBusService.create(config);
		final String topicPath = aggregate.getClass().getName();
		final TopicInfo topicInfo = new TopicInfo(topicPath);
		try {
			if (service.getTopic(topicPath) == null) {
				service.createTopic(topicInfo);
			}
			Long version = aggregate.getVersion();
			final ObjectMapper mapper = new ObjectMapper();
			for (final Event event : events) {
				final String eventString = mapper.writeValueAsString(event);
				final BrokeredMessage message = new BrokeredMessage(eventString);
				message.setProperty("version", ++version);
				message.setProperty("className", event.getClass().getName());
				service.sendTopicMessage(topicPath, message);
			}
		} catch (final ServiceException e) {
			throw new AggregateException(e.getMessage(), e);
		} catch (final JsonProcessingException e) {
			throw new AggregateException(e.getMessage(), e);
		}
	}
}
