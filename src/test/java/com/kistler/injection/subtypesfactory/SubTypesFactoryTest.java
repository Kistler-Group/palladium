package com.kistler.injection.subtypesfactory;

import com.kistler.injection.InjectorFactory;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SubTypesFactoryTest {

    private Injector injector;

    @BeforeEach
    void beforeAll() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
    }

    @Test
    void testSubTypesFactoryWithAbstractClasses() {
        AbstractClassFactory abstractClassFactory = injector.getInstance(AbstractClassFactory.class);
        AbstractClass test = abstractClassFactory.create("Test");
        assertTrue(test instanceof TestClass);
    }
}
