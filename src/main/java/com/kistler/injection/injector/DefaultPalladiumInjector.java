package com.kistler.injection.injector;

import com.kistler.injection.lifecycle.shutdown.ShutdownStrategy;
import com.kistler.injection.lifecycle.startup.StartupStrategy;
import com.google.inject.*;
import com.google.inject.spi.TypeConverterBinding;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic injector that looks up classes based on reflections
 * Also provides Startup and Shutdown strategies that can be centrally called to provide a hook to ensure shutdowns or startup initialization
 */
public class DefaultPalladiumInjector implements PalladiumInjector {
    private final Reflections        reflections;
    private final Provider<Injector> injectorProvider;

    DefaultPalladiumInjector(final Reflections reflections, final Provider<Injector> injectorProvider) {
        this.reflections = reflections;
        this.injectorProvider = injectorProvider;
    }

    @Override
    public <T> Set<Class<? extends T>> getClasses(Class<T> interfaceClass) {
        return reflections.getSubTypesOf(interfaceClass);
    }

    @Override
    public <T> Set<? extends T> getInstances(final Class<T> interfaceClass) {
        Set<T> instances = new HashSet<>();
        final Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(interfaceClass);
        for (Class<? extends T> cl : subTypesOf) {
            instances.add(injectorProvider.get().getInstance(cl));
        }
        return instances;
    }

    @Override
    public void startup() {
        StartupStrategy startupStrategy = getInstance(StartupStrategy.class);
        startupStrategy.startup();
    }

    @Override
    public void shutdown() {
        ShutdownStrategy shutdownStrategy = getInstance(ShutdownStrategy.class);
        shutdownStrategy.shutdown();
    }

    @Override
    public void injectMembers(final Object instance) {
        injectorProvider.get().injectMembers(instance);
    }

    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        return injectorProvider.get().getMembersInjector(typeLiteral);
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        return injectorProvider.get().getMembersInjector(type);
    }

    @Override
    public Map<Key<?>, Binding<?>> getBindings() {
        return injectorProvider.get().getBindings();
    }

    @Override
    public Map<Key<?>, Binding<?>> getAllBindings() {
        return injectorProvider.get().getAllBindings();
    }

    @Override
    public <T> Binding<T> getBinding(Key<T> key) {
        return injectorProvider.get().getBinding(key);
    }

    @Override
    public <T> Binding<T> getBinding(Class<T> type) {
        return injectorProvider.get().getBinding(type);
    }

    @Override
    public <T> Binding<T> getExistingBinding(Key<T> key) {
        return injectorProvider.get().getExistingBinding(key);
    }

    @Override
    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        return injectorProvider.get().findBindingsByType(type);
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        return injectorProvider.get().getProvider(key);
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return injectorProvider.get().getProvider(type);
    }

    @Override
    public <T> T getInstance(Key<T> key) {
        return injectorProvider.get().getInstance(key);
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return injectorProvider.get().getInstance(type);
    }

    @Override
    public Injector getParent() {
        return injectorProvider.get().getParent();
    }

    @Override
    public Injector createChildInjector(Iterable<? extends Module> modules) {
        return injectorProvider.get().createChildInjector(modules);
    }

    @Override
    public Injector createChildInjector(Module... modules) {
        return injectorProvider.get().createChildInjector(modules);
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        return injectorProvider.get().getScopeBindings();
    }

    @Override
    public Set<TypeConverterBinding> getTypeConverterBindings() {
        return injectorProvider.get().getTypeConverterBindings();
    }
}
