package com.kistler.injection.lifecycle;

import com.kistler.injection.InjectorFactory;
import com.kistler.injection.injector.PalladiumInjector;
import com.kistler.injection.lifecycle.startup.OnStartup;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class OnStartupTest {

    private Injector injector;

    @Test
    void testPostConstructCallOnNonSingletonClass() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        ClassWithOnStartup instance = injector.getInstance(ClassWithOnStartup.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.startup();
        assertEquals(ClassWithOnStartup.CHANGED_CONTENT, instance.getContent());
    }

    @Test
    void testPostConstructCallOnSingletonClass() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        SingletonClassWithOnStartup instance = injector.getInstance(SingletonClassWithOnStartup.class);
        PalladiumInjector palladiumInjector = injector.getInstance(PalladiumInjector.class);
        palladiumInjector.startup();
        assertEquals(ClassWithOnStartup.CHANGED_CONTENT, instance.getContent());
    }

    @Test
    void testStartupOnEagerSingletonWithoutInterface() throws NoSuchMethodException {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        // created together with the injector, before anything asked for it
        List<Object> targets = getStartupTargets(EagerSingletonClassWithOnStartup.class);
        assertEquals(1, targets.size());

        injector.getInstance(PalladiumInjector.class).startup();
        EagerSingletonClassWithOnStartup instance = injector.getInstance(EagerSingletonClassWithOnStartup.class);
        assertSame(targets.get(0), instance);
        assertEquals(ClassWithOnStartup.CHANGED_CONTENT, instance.getContent());
    }

    @Test
    void testEagerSingletonIsSharedByAllInterfaces() throws NoSuchMethodException {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        List<Object> targets = getStartupTargets(EagerSingletonWithInterfaces.class);
        assertEquals(1, targets.size());
        assertSame(targets.get(0), injector.getInstance(FirstEagerInterface.class));
        assertSame(targets.get(0), injector.getInstance(SecondEagerInterface.class));
        assertSame(targets.get(0), injector.getInstance(EagerSingletonWithInterfaces.class));
    }

    @Test
    void testStartupCreatesNoBindingForMissingSingleton() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
        injector.getInstance(PalladiumInjector.class).startup();
        // a just-in-time binding on this injector would keep a child injector from binding the class
        assertNull(injector.getExistingBinding(Key.get(SingletonClassWithOnStartup.class)));
    }

    private List<Object> getStartupTargets(Class<?> cls) throws NoSuchMethodException {
        return injector.getInstance(InstanceRegistry.class).getMethodTargets(OnStartup.class, cls.getMethod("onStartup"));
    }
}
