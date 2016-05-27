package com.microsoft.azure.framework.domain.service;

public class PersistenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PersistenceException() {
		super();
	}

	public PersistenceException(final String message) {
		super(message);
	}

	public PersistenceException(final Throwable cause) {
		super(cause);
	}

	public PersistenceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PersistenceException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
