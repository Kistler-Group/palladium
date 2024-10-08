package com.kistler.injection.lifecycle;

import com.kistler.injection.InjectorFactory;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostConstructTest {

    private Injector injector;

    @BeforeEach
    void beforeAll() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.injection"), this);
    }

    @Test
    void testPostConstructCall() {
        ClassWithPostConstruct instance = injector.getInstance(ClassWithPostConstruct.class);
        assertEquals(ClassWithPostConstruct.CHANGED_CONTENT, instance.getContent());
    }
}
