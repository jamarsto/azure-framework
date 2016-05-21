package com.microsoft.azure.framework.domain.entity;

import java.util.List;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.domain.event.Event;

public interface Entity {

	Boolean apply(List<Event> events);

	Boolean compensate(List<Event> events);

	List<Event> decide(Command command);

}
