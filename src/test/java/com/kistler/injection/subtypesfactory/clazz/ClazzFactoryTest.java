package com.kistler.injection.subtypesfactory.clazz;

import com.kistler.injection.InjectorFactory;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClazzFactoryTest {

    private Injector injector;

    @BeforeEach
    void beforeAll() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
    }

    @Test
    void testSubTypesFactoryWithClassesForSpecificClass() {
        ClazzFactory clazzFactory = injector.getInstance(ClazzFactory.class);
        ClazzInterface doubleClazz = clazzFactory.create(Double.class);
        assertTrue(doubleClazz instanceof DoubleClazz);
    }

    @Test
    void testSubTypesFactoryWithClassesForUnspecificClass() {
        ClazzFactory clazzFactory = injector.getInstance(ClazzFactory.class);
        ClazzInterface integerClazz = clazzFactory.create(Integer.class);
        assertTrue(integerClazz instanceof NumberClazz);
    }
}
