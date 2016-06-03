package com.microsoft.azure.framework.domain.event.impl;

public final class EventEntry {
	private String eventClassName;
	private String event;

	public EventEntry() {
	}

	public EventEntry(final String eventClassName, final String event) {
		this.eventClassName = eventClassName;
		this.event = event;
	}

	public String getEventClassName() {
		return eventClassName;
	}

	public String getEvent() {
		return event;
	}
}
