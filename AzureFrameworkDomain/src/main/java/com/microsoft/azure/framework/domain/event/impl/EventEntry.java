package com.microsoft.azure.framework.domain.event.impl;

import com.microsoft.azure.framework.domain.event.Event;

public final class EventEntry {
	private String eventClassName;
	private Event event;
	
	public EventEntry() {
	}
	
	public EventEntry(final Event event) {
		eventClassName = event.getClass().getName();
		this.event = event;
	}

	public String getEventClassName() {
		return eventClassName;
	}
	
	public Event getEvent() {
		return event;
	}
}
