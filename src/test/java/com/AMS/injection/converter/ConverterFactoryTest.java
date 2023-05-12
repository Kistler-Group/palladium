package com.AMS.injection.converter;

import com.AMS.injection.InjectorFactory;
import com.AMS.injection.converter.converters.Converter;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConverterFactoryTest {
    private static final String TEST_NAME = "Sample-123";
    private ConverterFactory converterFactory;

    @BeforeEach
    void beforeAll() {
        Injector injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
        converterFactory = injector.getInstance(ConverterFactory.class);
    }

    @Test
    void testSourceToTargetConversion() throws UnsupportedConversionException, ConversionException {
        Converter<ConversionSource, ConversionTarget> converter = converterFactory.getConverter(ConversionSource.class, ConversionTarget.class);

        ConversionSource conversionSource = new ConversionSource();
        conversionSource.setName(TEST_NAME);
        ConversionTarget result = converter.convert(conversionSource);
        assertEquals(TEST_NAME, result.getName());
    }

    @Test
    void testTargetToSourceConversion() throws UnsupportedConversionException, ConversionException {
        Converter<ConversionTarget, ConversionSource> converter = converterFactory.getConverter(ConversionTarget.class, ConversionSource.class);

        ConversionTarget conversionTarget = new ConversionTarget();
        conversionTarget.setName(TEST_NAME);
        ConversionSource result = converter.convert(conversionTarget);
        assertEquals(TEST_NAME, result.getName());
    }

    @Test
    void testIncompatibleTypes(){
        boolean threwException = false;
        try {
            Converter<String, ConversionSource> converter = converterFactory.getConverter(String.class, ConversionSource.class);
        } catch (UnsupportedConversionException e) {
            threwException = true;
        }

        assertTrue(threwException);
    }
}
