package com.microsoft.azure.framework.domain.service;

public final class ConcurrentUpdatePersistenceException extends PersistenceException {
	private static final long serialVersionUID = 1L;

	public ConcurrentUpdatePersistenceException() {
		super();
	}

	public ConcurrentUpdatePersistenceException(final String message) {
		super(message);
	}

	public ConcurrentUpdatePersistenceException(final Throwable cause) {
		super(cause);
	}

	public ConcurrentUpdatePersistenceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConcurrentUpdatePersistenceException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
