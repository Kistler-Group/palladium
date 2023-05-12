package com.AMS.injection.lifecycle;

import com.AMS.injection.annotation.InjectorBootstrap;
import com.AMS.injection.lifecycle.shutdown.OnShutdown;
import com.AMS.injection.lifecycle.startup.OnStartup;
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

public class LifecycleModule implements Module, TypeListener {

    private final InstanceRegistry instanceRegistry;

    @InjectorBootstrap
    public LifecycleModule() {
        instanceRegistry = new InstanceRegistry();
    }

    @Override
    public void configure(final Binder binder) {
        binder.bindListener(Matchers.any(), this);
        binder.bind(InstanceRegistry.class).toInstance(instanceRegistry);
    }

    @Override
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
        encounter.register((InjectionListener<I>) injectee -> {
            for (final Method method : Collections2.filter(Arrays.asList(injectee.getClass().getMethods()), MethodPredicate.ANNOTATED_WITH_ONSTARTUP)) {
                instanceRegistry.add(OnStartup.class, injectee, method);
            }
            for (final Method method : Collections2.filter(Arrays.asList(injectee.getClass().getMethods()), MethodPredicate.ANNOTATED_WITH_ONSHUTDOWN)) {
                instanceRegistry.add(OnShutdown.class, injectee, method);
            }
        });
    }
}
