package com.microsoft.azure.framework.domain.entity;

public final class EntityException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EntityException() {
		super();
	}

	public EntityException(final String message) {
		super(message);
	}

	public EntityException(final Throwable cause) {
		super(cause);
	}

	public EntityException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EntityException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
