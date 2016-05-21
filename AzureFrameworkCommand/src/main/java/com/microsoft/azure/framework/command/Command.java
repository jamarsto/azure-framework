package com.microsoft.azure.framework.command;

import java.util.UUID;

public interface Command {

	interface Builder {

		Command build();

	}

	interface BuilderFactory {

		Builder create();

	}

	UUID getAggregateID();

}
