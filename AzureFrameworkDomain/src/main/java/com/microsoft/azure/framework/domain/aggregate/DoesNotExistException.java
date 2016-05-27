package com.microsoft.azure.framework.domain.aggregate;

public final class DoesNotExistException extends AggregateException {
	private static final long serialVersionUID = 1L;

	public DoesNotExistException() {
		super();
	}

	public DoesNotExistException(final String message) {
		super(message);
	}

	public DoesNotExistException(final Throwable cause) {
		super(cause);
	}

	public DoesNotExistException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DoesNotExistException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
