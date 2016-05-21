package com.microsoft.azure.framework.domain.service;

public final class DomainServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DomainServiceException() {
		super();
	}

	public DomainServiceException(final String message) {
		super(message);
	}

	public DomainServiceException(final Throwable cause) {
		super(cause);
	}

	public DomainServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DomainServiceException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
