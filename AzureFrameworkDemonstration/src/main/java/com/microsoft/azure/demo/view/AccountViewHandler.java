package com.microsoft.azure.demo.view;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface AccountViewHandler {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response handleAccount(@Context ServletContext servletContext);

	@GET
	@Path("/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response handleAccount(@Context ServletContext servletContext, @PathParam("accountIdd") String accountIdString);

	@GET
	@Path("/{accountId}/transaction/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response handleTransaction(@Context ServletContext servletContext, @PathParam("accountId") String accountIdString);

	@GET
	@Path("/{accountId}/transaction/{transactionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response handleTransaction(@Context ServletContext servletContext, @PathParam("accountId") String accountIdString,
			@PathParam("transactionId") String transactionIddString);

}
