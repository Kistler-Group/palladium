package com.kistler.injection.converter;

import com.kistler.injection.converter.converters.Converter;
import com.kistler.injection.converter.converters.IdentityConverter;
import com.kistler.injection.converter.converters.LazyConverter;
import com.kistler.injection.converter.converters.ReverseConverter;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reflections-based provider of converters Finds and collects converters based on reflections and is in turn used as a
 * clean facade to supply a converter from one model type to a presentation type
 */
@Singleton
public class DefaultConverterFactory implements ConverterFactory {
    private final Table<TypeToken<?>, TypeToken<?>, Supplier<Converter<?, ?>>> converters        = HashBasedTable.create();
    private final List<TypeToken<?>>                                           specializedTokens = new ArrayList<TypeToken<?>>();
    private final Injector                                                     injector;
    private final Reflections                                                  reflections;

    @Inject
    public DefaultConverterFactory(Injector injector, Reflections reflections) {
        this.injector = injector;
        this.reflections = reflections;
    }

    @PostConstruct
    public void init() {
        Set<TypeToken<?>> knownTokens = new HashSet<TypeToken<?>>();

        for (Class<? extends Converter> converterClass : reflections.getSubTypesOf(Converter.class)) {

            //excludes derived interfaces, abstract classes and fallback converters from search
            if (!converterClass.isInterface() && !Modifier.isAbstract(converterClass.getModifiers()) && !converterClass.equals(ReverseConverter.class) && !converterClass.equals(IdentityConverter.class)) {

                Class<? extends Converter<?, ?>> cls = asConverterClass(converterClass);
                LazyConverter converter = new LazyConverter(injector, cls);
                TypeToken<?> modelType = converter.getModelType();
                TypeToken<?> presentationType = converter.getPresentationType();
                Supplier<Converter<?, ?>> existing = converters.put(modelType, presentationType, Suppliers.memoize(converter));
                Preconditions.checkState(existing == null, "Duplicate converter %s and %s.", converter, existing);

                if (converter.isReversible()) {
                    existing = converters.put(presentationType, modelType, Suppliers.memoize(converter.reverse()));
                    Preconditions.checkState(existing == null, "Duplicate converter %s and %s.", converter.reverse(), existing);
                }

                knownTokens.add(modelType);
                knownTokens.add(presentationType);
            }
        }

        // finds specialised tokens to offer a better converter lookup after initialization
        while (!knownTokens.isEmpty()) {
            TypeToken<?> modelType = knownTokens.iterator().next();
            for (TypeToken<?> knownToken : knownTokens) {
                if (modelType.isSupertypeOf(knownToken)) {
                    modelType = knownToken;
                }
            }
            specializedTokens.add(modelType);
            knownTokens.remove(modelType);
        }
    }

    private Class<? extends Converter<?, ?>> asConverterClass(Class<?> converterClass) {
        return (Class<? extends Converter<?, ?>>) converterClass;
    }

    @Override
    public <M, P> Converter<M, P> getConverter(Class<M> modelType, TypeToken<P> presentationType) throws UnsupportedConversionException {
        return getConverter(TypeToken.of(modelType), presentationType);
    }

    @Override
    public <M, P> Converter<M, P> getConverter(TypeToken<M> modelType, Class<P> presentationType) throws UnsupportedConversionException {
        return getConverter(modelType, TypeToken.of(presentationType));
    }

    @Override
    public <M, P> Converter<M, P> getConverter(Class<M> modelType, Class<P> presentationType) throws UnsupportedConversionException {
        return getConverter(TypeToken.of(modelType), TypeToken.of(presentationType));
    }

    /**
     * Looks up a converter from modelType to presentationType. From the set of registered converter classes it chooses
     * the one with the source type closest to modelType (most specialized) and with the targetType closest to
     * presentationType (least specialized). If the presentationType is directly assignable from the modelType and no
     * explicit converter is defined for that combination no conversion is performed and an identity converter is
     * returned.
     *
     * @throws UnsupportedConversionException Throws an exception if no converter can be found in the current context
     */
    @Override
    public <M, P> Converter<M, P> getConverter(final TypeToken<M> modelType, final TypeToken<P> presentationType) throws UnsupportedConversionException {
        Supplier<Converter<?, ?>> converter = converters.get(modelType, presentationType);
        if (converter != null) {
            return (Converter<M, P>) converter.get();
        }
        if (presentationType.isSupertypeOf(modelType)) {
            return (Converter<M, P>) IdentityConverter.of(modelType);
        }

        Iterable<TypeToken<?>> acceptedRows = ImmutableList.copyOf(Collections2.filter(specializedTokens, input -> {
            assert input != null;
            return input.isSupertypeOf(modelType);
        }));
        Iterable<TypeToken<?>> acceptedColumns = ImmutableList.copyOf(Collections2.filter(Lists.reverse(specializedTokens), presentationType::isSupertypeOf));

        for (TypeToken<?> acceptedRow : acceptedRows) {
            for (TypeToken<?> acceptedColumn : acceptedColumns) {
                Supplier<Converter<?, ?>> lazyConverter = converters.get(acceptedRow, acceptedColumn);
                if (lazyConverter != null) {
                    converters.put(modelType, presentationType, lazyConverter);
                    return (Converter<M, P>) lazyConverter.get();
                }
            }
        }

        throw new UnsupportedConversionException(modelType, presentationType);
    }

    // for test purposes
    @Deprecated
    public void reinit() {
        converters.clear();
        specializedTokens.clear();
        init();
    }
}
