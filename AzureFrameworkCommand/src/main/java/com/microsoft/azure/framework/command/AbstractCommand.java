package com.microsoft.azure.framework.command;

import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.azure.framework.precondition.PreconditionService;

public abstract class AbstractCommand implements Command {
	@Autowired
	protected PreconditionService preconditionService;
}
