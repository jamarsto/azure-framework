package com.microsoft.azure.demo.impl;

import java.util.UUID;

import javax.persistence.PersistenceException;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.microsoft.azure.demo.CommandHandler;
import com.microsoft.azure.demo.CommandService;
import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.command.CommandException;
import com.microsoft.azure.framework.command.processor.CommandProcessor;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.service.DomainServiceException;
import com.microsoft.azure.framework.precondition.PreconditionException;

@Path("/command")
public class SimpleCommandHandler implements CommandHandler {

	@GET
	@Path("/new/uuid")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response handle() {
		return Response.ok(new UniqueID(UUID.randomUUID())).build();
	}

	@PUT
	@Path("/{commandName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response handle(final @Context ServletContext servletContext,
			final @PathParam("commandName") String commandName, final String json) {
		try {
			final CommandService commandService = getBean(servletContext, CommandService.class);
			final CommandProcessor commandProcessor = getBean(servletContext, CommandProcessor.class);
			final Command command = commandService.createCommand(commandName, json);
			commandProcessor.doCommand(command);
			return Response.ok(new UniqueID(command.getAggregateId())).build();
		} catch (final AggregateException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Error(Error.AGGREGATE, e.getMessage()))
					.build();
		} catch (final CommandException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Error(Error.COMMAND, e.getMessage()))
					.build();
		} catch (final DomainServiceException e) {
			return Response.status(Status.BAD_REQUEST).entity(new Error(Error.DOMAIN_SERVICE, e.getMessage())).build();
		} catch (final PersistenceException e) {
			return Response.status(Status.BAD_REQUEST).entity(new Error(Error.PERSISTENCE, e.getMessage())).build();
		} catch (final PreconditionException e) {
			return Response.status(Status.PRECONDITION_FAILED).entity(new Error(Error.PRECONDITION, e.getMessage()))
					.build();
		} catch (final RuntimeException e) {
			System.out.println(json);
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Error(Error.RUNTIME, e.getMessage()))
					.build();
		}
	}

	private <T> T getBean(final ServletContext servletContext, final Class<T> clazz) {
		return WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(clazz);
	}
}
