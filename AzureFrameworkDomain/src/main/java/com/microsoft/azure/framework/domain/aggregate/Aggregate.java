package com.microsoft.azure.framework.domain.aggregate;

import java.util.List;
import java.util.UUID;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.event.Event;

public interface Aggregate {

	Boolean apply(List<Event> events);

	void commit();

	Boolean compensate(List<Event> events);

	List<Event> decide(Command command);

	UUID getID();

	Long getVersion();

	void rollback();

}
