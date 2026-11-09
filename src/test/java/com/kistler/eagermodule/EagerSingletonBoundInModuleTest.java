package com.kistler.eagermodule;

import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.kistler.injection.InjectorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class EagerSingletonBoundInModuleTest {

    @Test
    void testModuleRepeatingTheBindingOfAnEagerSingleton() {
        Injector injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.eagermodule"), this);
        assertSame(injector.getInstance(ModuleBoundService.class), injector.getInstance(ModuleBoundServiceImpl.class));
    }
}
