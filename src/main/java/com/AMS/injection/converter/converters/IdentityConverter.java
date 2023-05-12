package com.AMS.injection.converter.converters;

import com.AMS.injection.converter.ConversionException;
import com.google.common.reflect.TypeToken;

/**
 * Basic implementation of Converter that serves as a fallback NoOp-Converter
 */
public class IdentityConverter<P> implements Converter<P, P> {
    private final TypeToken<P> token;

    public static <T> IdentityConverter<T> of(TypeToken<T> token) {
        return new IdentityConverter(token);
    }

    private IdentityConverter(TypeToken<P> token) {
        this.token = token;
    }

    @Override
    public P convert(P object) throws ConversionException {
        return object;
    }

    @Override
    public TypeToken<P> getModelType() {
        return token;
    }

    @Override
    public TypeToken<P> getPresentationType() {
        return token;
    }
}
