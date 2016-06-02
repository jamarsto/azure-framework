package com.microsoft.azure.framework.eventbus;

import com.microsoft.azure.framework.domain.aggregate.Aggregate;

public interface EventBus {

	void publish(Aggregate aggregate);

}
