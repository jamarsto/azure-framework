package com.microsoft.azure.framework.eventbus;

import java.util.List;

import com.microsoft.azure.framework.domain.aggregate.Aggregate;
import com.microsoft.azure.framework.domain.event.Event;

public interface EventBus {

	void publish(Aggregate aggregate, List<Event> events);

}
