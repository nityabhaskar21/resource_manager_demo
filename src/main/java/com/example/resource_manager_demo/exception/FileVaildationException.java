package com.example.resource_manager_demo.exception;

public class FileVaildationException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8794341404705105629L;

	public FileVaildationException() {
		super("File is not Valid for Processing.");
	}

	public FileVaildationException(String msg) {
		super(msg);
	}

}
