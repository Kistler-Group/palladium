package com.kistler.injection.subtypefactory.type;

public class TypeParseException extends Exception {
	public TypeParseException(String message, Exception cause) {
		super(message, cause);
	}

	public TypeParseException(String message) {
		super(message);
	}
}
