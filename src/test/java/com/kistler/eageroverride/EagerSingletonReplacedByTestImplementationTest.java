package com.kistler.eageroverride;

import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.kistler.injection.InjectorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EagerSingletonReplacedByTestImplementationTest {

    @Test
    void testAllKeysShareTheTestImplementation() {
        Injector injector = InjectorFactory.createInjector(Sets.newHashSet("com.kistler.eageroverride"), this);
        EagerService service = injector.getInstance(EagerService.class);
        assertTrue(service instanceof TestEagerService);
        assertSame(service, injector.getInstance(DefaultEagerService.class));
        assertSame(service, injector.getInstance(TestEagerService.class));
    }
}
