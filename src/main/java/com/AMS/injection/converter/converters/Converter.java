package com.AMS.injection.converter.converters;

import com.AMS.injection.converter.ConversionException;
import com.google.common.reflect.TypeToken;

/**
 * Interface for converting content from a given model type to an intended type for presentation or further processing
 * @param <M> Original modeltype
 * @param <P> Target presentation type
 */
public interface Converter<M, P> {
	P convert(M object) throws ConversionException;

	TypeToken<M> getModelType();

	TypeToken<P> getPresentationType();
}
