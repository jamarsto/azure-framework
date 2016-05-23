package com.microsoft.azure.demo;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public interface CommandHandler {
	
	@GET
	@Path("/new/uuid")
	@Produces("application/json")
	Response handle();

	@PUT
	@Path("/{commandName}")
	@Produces("application/json")
	@Consumes("application/json")
	Response handle(@Context ServletContext servletContext, @PathParam("commandName") String commandName, String json);

}
