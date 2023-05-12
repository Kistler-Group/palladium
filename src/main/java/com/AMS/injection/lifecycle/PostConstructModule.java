package com.AMS.injection.lifecycle;

import com.AMS.injection.annotation.InjectorBootstrap;
import com.google.common.collect.Collections2;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Bootstrap module that registers PostConstruct methods to invoke them after a type/injectable has been found
 */
public class PostConstructModule implements Module, TypeListener {
    @InjectorBootstrap
    public PostConstructModule() {
    }

    /**
     * Binds this class as a listener to all injections/to all classes
     */
    @Override
    public void configure(Binder binder) {
        binder.bindListener(Matchers.any(), this);
    }

    /**
     * Method that gets called after injection, and in turn calls the @PostConstruct methods
     */
    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        encounter.register((InjectionListener<I>) injectee -> {
            for (final Method postConstructMethod : Collections2.filter(Arrays.asList(injectee.getClass().getMethods()), MethodPredicate.VALID_POSTCONSTRUCT)) {
                try {
                    postConstructMethod.invoke(injectee);
                } catch (final Exception e) {
                    throw new RuntimeException(String.format("@PostConstruct %s", postConstructMethod), e);
                }
            }
        });
    }
}
