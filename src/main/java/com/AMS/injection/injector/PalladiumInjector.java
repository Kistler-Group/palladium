package com.AMS.injection.injector;

import com.google.inject.Injector;

import java.util.Set;

/**
 * Extension of Guice Injector that offers some more sugar methods, as well as dedicated startup and shutdowns
 */
public interface PalladiumInjector extends Injector {

    <T> Set<Class<? extends T>> getClasses(Class<T> interfaceClass);

    <T> Set<? extends T> getInstances(Class<T> interfaceClass);

    void startup();

    void shutdown();
}
