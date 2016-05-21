package com.microsoft.azure.framework.precondition;

public final class PreconditionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PreconditionException() {
		super();
	}

	public PreconditionException(final String message) {
		super(message);
	}

	public PreconditionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PreconditionException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PreconditionException(Throwable cause) {
		super(cause);
	}
}
