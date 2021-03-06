package com.microsoft.azure.demo.view.impl;

import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.microsoft.azure.demo.view.AccountViewHandler;
import com.microsoft.azure.demo.view.bean.AccountBean;
import com.microsoft.azure.demo.view.bean.ErrorBean;
import com.microsoft.azure.demo.view.bean.ResultListBean;
import com.microsoft.azure.demo.view.bean.TransactionBean;
import com.microsoft.azure.demo.view.persistence.AccountViewDAO;
import com.microsoft.azure.demo.view.persistence.TransactionViewDAO;

@Path("/query/account")
public final class SimpleAccountViewHandler extends AbstractViewHandler implements AccountViewHandler {
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response handleAccount(final @Context ServletContext servletContext) {
		try {
			final AccountViewDAO accountViewDAO = getBean(servletContext, AccountViewDAO.class);
			final List<AccountBean> accountBeanList = accountViewDAO.getAccounts();
			return Response.ok(new ResultListBean(accountBeanList)).build();
		} catch (final RuntimeException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ErrorBean(e.getMessage())).build();
		}
	}

	@GET
	@Path("/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response handleAccount(final @Context ServletContext servletContext,
			final @PathParam("accountId") String accountIdString) {
		try {
			final AccountViewDAO accountViewDAO = getBean(servletContext, AccountViewDAO.class);
			final UUID accountId = UUID.fromString(accountIdString);
			final AccountBean accountBean = accountViewDAO.getAccount(accountId);
			if (accountBean != null) {
				return Response.ok(accountBean).build();
			}
			return Response.ok().build();
		} catch (final RuntimeException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ErrorBean(e.getMessage())).build();
		}
	}

	@GET
	@Path("/{accountId}/transaction/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response handleTransaction(final @Context ServletContext servletContext,
			final @PathParam("accountId") String accountIdString) {
		try {
			final TransactionViewDAO transactionViewDAO = getBean(servletContext, TransactionViewDAO.class);
			final UUID accountId = UUID.fromString(accountIdString);
			final List<TransactionBean> transactionBeanList = transactionViewDAO.getTransactions(accountId);
			return Response.ok(new ResultListBean(transactionBeanList)).build();
		} catch (final RuntimeException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ErrorBean(e.getMessage())).build();
		}
	}

	@GET
	@Path("/{accountId}/transaction/{transactionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response handleTransaction(final @Context ServletContext servletContext,
			final @PathParam("accountId") String accountIdString,
			final @PathParam("transactionId") String transactionIdString) {
		try {
			final TransactionViewDAO transactionViewDAO = getBean(servletContext, TransactionViewDAO.class);
			final UUID accountId = UUID.fromString(accountIdString);
			final UUID transactionId = UUID.fromString(transactionIdString);
			final TransactionBean transactionBean = transactionViewDAO.getTransaction(accountId, transactionId);
			if (transactionBean != null) {
				return Response.ok(transactionBean).build();
			}
			return Response.ok().build();
		} catch (final RuntimeException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ErrorBean(e.getMessage())).build();
		}
	}
}
