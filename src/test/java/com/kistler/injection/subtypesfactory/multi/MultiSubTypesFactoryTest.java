package com.kistler.injection.subtypesfactory.multi;

import com.kistler.injection.InjectorFactory;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiSubTypesFactoryTest {

    private Injector injector;

    @BeforeEach
    void beforeAll() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
    }

    @Test
    void testMultiSubTypesFactory() {
        final TestFactory factory = injector.getInstance(TestFactory.class);
        final Target keyTarget1 = factory.create("Key1");
        final Target keyTarget2 = factory.create("Key2");
        assertTrue(keyTarget1 instanceof DefaultTarget);
        assertTrue(keyTarget2 instanceof DefaultTarget);
    }
}
