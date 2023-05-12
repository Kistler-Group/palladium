package com.kistler.injection.converter;

import com.kistler.injection.converter.converters.Converter;
import com.google.common.reflect.TypeToken;

public interface ConverterFactory {
	<M, P> Converter<M, P> getConverter(Class<M> modelType, TypeToken<P> presentationType) throws UnsupportedConversionException;

	<M, P> Converter<M, P> getConverter(TypeToken<M> modelType, Class<P> presentationType) throws UnsupportedConversionException;

	<M, P> Converter<M, P> getConverter(Class<M> modelType, Class<P> presentationType) throws UnsupportedConversionException;

	<M, P> Converter<M, P> getConverter(TypeToken<M> modelType, TypeToken<P> presentationType) throws UnsupportedConversionException;
}
