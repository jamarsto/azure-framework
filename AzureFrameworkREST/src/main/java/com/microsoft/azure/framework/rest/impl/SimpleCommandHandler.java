package com.microsoft.azure.framework.rest.impl;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.microsoft.azure.framework.command.Command;
import com.microsoft.azure.framework.command.CommandException;
import com.microsoft.azure.framework.command.processor.CommandProcessor;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.aggregate.AlreadyExistsException;
import com.microsoft.azure.framework.domain.aggregate.DoesNotExistException;
import com.microsoft.azure.framework.domain.service.ConcurrentUpdatePersistenceException;
import com.microsoft.azure.framework.domain.service.DomainServiceException;
import com.microsoft.azure.framework.precondition.PreconditionException;
import com.microsoft.azure.framework.rest.CommandHandler;
import com.microsoft.azure.framework.rest.CommandService;

@Path("/command")
public class SimpleCommandHandler implements CommandHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

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
		} catch (final AlreadyExistsException | DoesNotExistException e) {
			return Response.status(Status.BAD_REQUEST).entity(new Error(Error.AGGREGATE_EXISTANCE, e.getMessage()))
					.build();
		} catch (final AggregateException e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Error(Error.AGGREGATE, e.getMessage()))
					.build();
		} catch (final CommandException e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Error(Error.COMMAND, e.getMessage()))
					.build();
		} catch (final DomainServiceException e) {
			LOGGER.info(e.getMessage(), e);
			return Response.status(Status.BAD_REQUEST).entity(new Error(Error.DOMAIN_SERVICE, e.getMessage())).build();
		} catch (final ConcurrentUpdatePersistenceException e) {
			return Response.status(Status.BAD_REQUEST)
					.entity(new Error(Error.PERSISTENCE_CONCURRENT_UPDATE, e.getMessage())).build();
		} catch (final PersistenceException e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Error(Error.PERSISTENCE, e.getMessage())).build();
		} catch (final PreconditionException e) {
			return Response.status(Status.PRECONDITION_FAILED).entity(new Error(Error.PRECONDITION, e.getMessage()))
					.build();
		} catch (final RuntimeException e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Error(Error.RUNTIME, e.getMessage()))
					.build();
		}
	}

	private <T> T getBean(final ServletContext servletContext, final Class<T> clazz) {
		return WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(clazz);
	}
}
