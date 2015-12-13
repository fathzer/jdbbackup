package com.fathzer.jdbbackup;

public class InvalidArgument extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidArgument(String message) {
		super(message);
	}

	public InvalidArgument(Throwable cause) {
		super(cause);
	}
}
