package com.kistler.injection.converter;

import com.kistler.injection.converter.converters.DualConverter;
import com.google.common.reflect.TypeToken;

public class SourceToTargetConverter implements DualConverter<ConversionSource, ConversionTarget> {
    @Override
    public ConversionSource reverse(ConversionTarget object) throws ConversionException {
        ConversionSource conversionSource = new ConversionSource();
        conversionSource.setName(object.getName());
        return conversionSource;
    }

    @Override
    public ConversionTarget convert(ConversionSource object) throws ConversionException {
        ConversionTarget conversionTarget = new ConversionTarget();
        conversionTarget.setName(object.getName());
        return conversionTarget;
    }

    @Override
    public TypeToken<ConversionSource> getModelType() {
        return TypeToken.of(ConversionSource.class);
    }

    @Override
    public TypeToken<ConversionTarget> getPresentationType() {
        return TypeToken.of(ConversionTarget.class);
    }
}
