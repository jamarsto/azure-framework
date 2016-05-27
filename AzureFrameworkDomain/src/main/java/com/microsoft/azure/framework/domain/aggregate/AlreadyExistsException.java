package com.microsoft.azure.framework.domain.aggregate;

public final class AlreadyExistsException extends AggregateException {
	private static final long serialVersionUID = 1L;

	public AlreadyExistsException() {
		super();
	}

	public AlreadyExistsException(final String message) {
		super(message);
	}

	public AlreadyExistsException(final Throwable cause) {
		super(cause);
	}

	public AlreadyExistsException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public AlreadyExistsException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
