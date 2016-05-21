package com.microsoft.azure.framework.eventstore.persistence;

public final class DeserializationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DeserializationException() {
		super();
	}

	public DeserializationException(final String message) {
		super(message);
	}

	public DeserializationException(final Throwable cause) {
		super(cause);
	}

	public DeserializationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DeserializationException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
