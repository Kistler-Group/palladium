package com.AMS.injection.converter.converters;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Basic supplier for a concrete implementation of a converter
 * Although Guava Supplier is marked as legacy, we use the {@link Suppliers#memoize(Supplier)} functionality, which has no equivalent in basic Java Functions
 */
public class LazyConverter implements Supplier<Converter<?, ?>> {
    private final Injector                         injector;
    private final Class<? extends Converter<?, ?>> converterClass;
    private final TypeToken<?>                     modelType;
    private final TypeToken<?>                     presentationType;

    public LazyConverter(Injector injector, Class<? extends Converter<?, ?>> converterClass) {
        this.injector = injector;
        this.converterClass = converterClass;
        TypeToken<?>[] converterTypes = determineTypes(converterClass);
        modelType = converterTypes[0];
        presentationType = converterTypes[1];
    }

    /**
     * Extracts the generics from the converter class that should be supplied
     * Note that it strictly considers the first two type arguments of the given interface, since that is required by converters
     * A rare case in which this would lead to problems is if the class implements an interface with three generics, the latter two of which are used to type the extended Converter or DualConverter interface
     */
    private TypeToken<?>[] determineTypes(Class<?> converterClass) {
        Type[] genericInterfaces = converterClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedInterface = (ParameterizedType) genericInterface;
                Type rawType = parameterizedInterface.getRawType();
                if (rawType.equals(Converter.class) || rawType.equals(DualConverter.class)) {
                    Type[] actualTypeArguments = parameterizedInterface.getActualTypeArguments();
                    TypeToken<?> modelType = TypeToken.of(actualTypeArguments[0]);
                    TypeToken<?> presentationType = TypeToken.of(actualTypeArguments[1]);
                    return new TypeToken<?>[]{modelType, presentationType};
                }
            }
        }
        return null;
    }

    public TypeToken<?> getModelType() {
        return modelType;
    }

    public TypeToken<?> getPresentationType() {
        return presentationType;
    }

    @Override
    public Converter<?, ?> get() {
        return injector.getInstance(converterClass);
    }

    public Supplier<Converter<?, ?>> reverse() {
        return new Supplier<Converter<?, ?>>() {
            @Override
            public Converter<?, ?> get() {
                return new ReverseConverter<>((DualConverter<Object, Object>) LazyConverter.this.get());
            }

            @Override
            public String toString() {
                return "Reverse " + LazyConverter.this;
            }
        };
    }

    public boolean isReversible() {
        return DualConverter.class.isAssignableFrom(converterClass);
    }

    @Override
    public String toString() {
        return "Lazy " + converterClass.getSimpleName();
    }
}
