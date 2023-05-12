package com.kistler.injection.converter;

public class ConversionException extends Exception {
	private static final long serialVersionUID = 3016109579148474295L;

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}
}
