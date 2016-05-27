package com.microsoft.azure.framework.domain.aggregate;

public class AggregateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AggregateException() {
		super();
	}

	public AggregateException(final String message) {
		super(message);
	}

	public AggregateException(final Throwable cause) {
		super(cause);
	}

	public AggregateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public AggregateException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
