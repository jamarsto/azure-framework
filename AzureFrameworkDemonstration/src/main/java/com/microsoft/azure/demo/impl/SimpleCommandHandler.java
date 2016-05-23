package com.microsoft.azure.demo.impl;

import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.microsoft.azure.demo.CommandService;
import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.command.CommandException;
import com.microsoft.azure.framework.command.processor.CommandProcessor;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.service.DomainServiceException;
import com.microsoft.azure.framework.eventstore.persistence.SerializationException;
import com.microsoft.azure.framework.precondition.PreconditionException;

@Path("/command")
public class SimpleCommandHandler {

	@GET
	@Path("new/uuid")
	@Produces("application/json")
	public Response handle() {
		return Response.ok(new UniqueID(UUID.randomUUID())).build();
	}

	@PUT
	@Path("{commandName}")
	@Produces("application/json")
	public Response handle(final @Context ServletContext servletContext,
			final @PathParam("commandName") String commandName, final String json) {
		try {
			final CommandService commandService = getBean(servletContext, CommandService.class);
			final CommandProcessor commandProcessor = getBean(servletContext, CommandProcessor.class);
			final Command command = commandService.createCommand(commandName, json);
			commandProcessor.doCommand(command);

			command.getAggregateID();
		} catch (final CommandException | DomainServiceException | AggregateException | SerializationException
				| PreconditionException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		return Response.ok().build();
	}

	private <T> T getBean(final ServletContext servletContext, final Class<T> clazz) {
		return WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(clazz);
	}
}
