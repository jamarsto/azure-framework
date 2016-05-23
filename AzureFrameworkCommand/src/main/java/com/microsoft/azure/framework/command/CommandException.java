package com.microsoft.azure.framework.command;

public final class CommandException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CommandException() {
		super();
	}

	public CommandException(final String message) {
		super(message);
	}

	public CommandException(final Throwable cause) {
		super(cause);
	}

	public CommandException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CommandException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
