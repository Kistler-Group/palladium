package com.kistler.injection.lifecycle;

import com.kistler.injection.InjectorFactory;
import com.kistler.injection.injector.PalladiumInjector;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnStartupTest {

    private Injector injector;

    @Test
    void testPostConstructCallOnNonSingletonClass() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
        ClassWithOnStartup instance = injector.getInstance(ClassWithOnStartup.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.startup();
        assertEquals(ClassWithOnStartup.CHANGED_CONTENT, instance.getContent());
    }

    @Test
    void testPostConstructCallOnSingletonClass() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
        SingletonClassWithOnStartup instance = injector.getInstance(SingletonClassWithOnStartup.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.startup();
        assertEquals(ClassWithOnStartup.CHANGED_CONTENT, instance.getContent());
    }
}
