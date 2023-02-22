package com.fathzer.jdbbackup;

public class InvalidArgumentException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public InvalidArgumentException(String message) {
		super(message);
	}

	public InvalidArgumentException(Throwable cause) {
		super(cause);
	}
}
