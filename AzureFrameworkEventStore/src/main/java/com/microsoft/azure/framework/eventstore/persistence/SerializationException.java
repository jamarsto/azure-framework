package com.microsoft.azure.framework.eventstore.persistence;

public final class SerializationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SerializationException() {
		super();
	}

	public SerializationException(final String message) {
		super(message);
	}

	public SerializationException(final Throwable cause) {
		super(cause);
	}

	public SerializationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public SerializationException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
