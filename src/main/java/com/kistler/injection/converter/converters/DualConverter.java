package com.kistler.injection.converter.converters;

import com.kistler.injection.converter.ConversionException;

/**
 * Interface for converters that are responsible for changing values between types back and forth
 */
public interface DualConverter<M, P> extends Converter<M, P> {
	M reverse(P object) throws ConversionException;
}
