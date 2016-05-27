package com.microsoft.azure.framework.eventstore;

public final class ConcurrentUpdateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConcurrentUpdateException() {
		super();
	}

	public ConcurrentUpdateException(final String message) {
		super(message);
	}

	public ConcurrentUpdateException(final Throwable cause) {
		super(cause);
	}

	public ConcurrentUpdateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConcurrentUpdateException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
