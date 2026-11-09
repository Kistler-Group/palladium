package com.kistler.injection.lifecycle;

import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.kistler.injection.InjectorFactory;
import com.kistler.injection.injector.PalladiumInjector;
import com.kistler.injection.lifecycle.shutdown.OnShutdown;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnShutdownTest {

    private Injector injector;

    @Test
    void testPostConstructCallForSingleton() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        SingletonClassWithOnShutdown instance = injector.getInstance(SingletonClassWithOnShutdown.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.shutdown();
        assertEquals(ClassWithOnShutdown.CHANGED_CONTENT, instance.getContent());
    }

    @Test
    void testPostConstructCallForNonSingleton() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        ClassWithOnShutdown instance = injector.getInstance(ClassWithOnShutdown.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.shutdown();
        assertEquals(ClassWithOnShutdown.CHANGED_CONTENT, instance.getContent());
    }

    @Test
    void testShutdownWithoutAnyInjectedInstance() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        // precondition: no instance with an @OnShutdown method exists, e.g. none created eagerly
        InstanceRegistry instanceRegistry = injector.getInstance(InstanceRegistry.class);
        for (Method method : injector.getInstance(Reflections.class).getMethodsAnnotatedWith(OnShutdown.class)) {
            assertTrue(instanceRegistry.getMethodTargets(OnShutdown.class, method).isEmpty(), method + " has an instance");
        }
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        assertDoesNotThrow(palladiumInjector::shutdown);
    }
}
