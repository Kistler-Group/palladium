package com.kistler.injection.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import org.reflections.Reflections;

/**
 * Binds the DefaultPalladiumInjector to the interface in order to establish a base that can be extended or exchanged
 */
public class PalladiumInjectorModule extends AbstractModule {

    private final Reflections reflections;

    public PalladiumInjectorModule(Reflections reflections) {
        this.reflections = reflections;
    }

    @Override
    protected void configure() {
        bind(PalladiumInjector.class).toInstance(
                new DefaultPalladiumInjector(
                        this.reflections,
                        getProvider(Injector.class)
                )
        );
    }
}
