package com.AMS.injection.converter.converters;

import com.AMS.injection.converter.ConversionException;
import com.google.common.reflect.TypeToken;

/**
 * DualConverter that reverts the conversion of the given base DualConverter
 */
public class ReverseConverter<M, P> implements DualConverter<M, P> {
	private final DualConverter<P, M> baseConverter;

	public ReverseConverter(DualConverter<P, M> baseConverter) {
		this.baseConverter = baseConverter;
	}

	@Override
	public M reverse(P object) throws ConversionException, ConversionException {
		return baseConverter.convert(object);
	}

	@Override
	public P convert(M object) throws ConversionException {
		return baseConverter.reverse(object);
	}

	@Override
	public TypeToken<M> getModelType() {
		return baseConverter.getPresentationType();
	}

	@Override
	public TypeToken<P> getPresentationType() {
		return baseConverter.getModelType();
	}

	@Override
	public int hashCode() {
		return baseConverter.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReverseConverter<?, ?>) {
			return baseConverter.equals(((ReverseConverter<?, ?>) obj).baseConverter);
		}
		return baseConverter.equals(obj);
	}
}
