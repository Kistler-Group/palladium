package com.kistler.injection.lifecycle;

import com.kistler.injection.lifecycle.startup.OnStartup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceRegistryTest {

    @Test
    void testMethodTargetsOfEmptyRegistry() throws NoSuchMethodException {
        InstanceRegistry instanceRegistry = new InstanceRegistry();
        assertTrue(instanceRegistry.getMethodTargets(OnStartup.class, ClassWithOnStartup.class.getMethod("onStartup")).isEmpty());
    }
}
