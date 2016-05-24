package com.microsoft.azure.framework.eventbus.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.framework.domain.event.Event;

public class TestHarness {
	public static void main(final String[] args) {
		try {
			final List<Event> events = new ArrayList<Event>();
			final Event event = new TestEvent(UUID.randomUUID());
			events.add(event);
			final ObjectMapper mapper = new ObjectMapper();
			String eventString = mapper.writeValueAsString(event);
			final String eventStrings = mapper.writeValueAsString(events);
			System.out.println(eventString);
			System.out.println(eventStrings);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public static final class TestEvent implements Event {
		private UUID id;
		
		public TestEvent(final UUID id) {
			this.id = id;
		}
		
		public UUID getId() {
			return id;
		}
	}
}
