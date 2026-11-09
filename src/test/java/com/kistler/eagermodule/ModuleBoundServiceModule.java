package com.kistler.eagermodule;

import com.google.inject.AbstractModule;
import com.kistler.injection.annotation.InjectorBootstrap;

/**
 * Repeats the binding that ReflectionsModule creates for {@link ModuleBoundServiceImpl}.
 */
public class ModuleBoundServiceModule extends AbstractModule {
    @InjectorBootstrap
    public ModuleBoundServiceModule() {
    }

    @Override
    protected void configure() {
        bind(ModuleBoundService.class).to(ModuleBoundServiceImpl.class).asEagerSingleton();
    }
}
