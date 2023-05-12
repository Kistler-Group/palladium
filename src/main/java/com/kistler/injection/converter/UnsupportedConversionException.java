package com.kistler.injection.converter;

import com.google.common.reflect.TypeToken;

public class UnsupportedConversionException extends Exception {
	public <M, P> UnsupportedConversionException(TypeToken<M> modelType, TypeToken<P> presentationType) {
		super("Unsupported conversion from " + modelType + " to " + presentationType);
	}
}
