package com.kistler.injection.lifecycle;

import com.kistler.injection.InjectorFactory;
import com.kistler.injection.injector.PalladiumInjector;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnShutdownTest {

    private Injector injector;

    @Test
    void testPostConstructCallForSingleton() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
        SingletonClassWithOnShutdown instance = injector.getInstance(SingletonClassWithOnShutdown.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.shutdown();
        assertEquals(ClassWithOnShutdown.CHANGED_CONTENT, instance.getContent());
    }

    @Test
    void testPostConstructCallForNonSingleton() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
        ClassWithOnShutdown instance = injector.getInstance(ClassWithOnShutdown.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.shutdown();
        assertEquals(ClassWithOnShutdown.CHANGED_CONTENT, instance.getContent());
    }
}
