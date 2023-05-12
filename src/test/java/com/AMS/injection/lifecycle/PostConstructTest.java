package com.AMS.injection.lifecycle;

import com.AMS.injection.InjectorFactory;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostConstructTest {

    private Injector injector;

    @BeforeEach
    void beforeAll() {
        injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
    }

    @Test
    void testPostConstructCall() {
        ClassWithPostConstruct instance = injector.getInstance(ClassWithPostConstruct.class);
        assertEquals(ClassWithPostConstruct.CHANGED_CONTENT, instance.getContent());
    }
}
