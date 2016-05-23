package com.microsoft.azure.demo.command;

import java.util.UUID;

import com.microsoft.azure.framework.command.Command;

public interface CreateAccount extends Command {

	interface Builder extends Command.Builder {

		CreateAccount build();

		Builder buildAccountId(UUID id);

	}

	interface BuilderFactory extends Command.BuilderFactory {

		Builder create();

	}

}
