package com.AMS.injection.override;

import com.AMS.injection.InjectorFactory;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OverrideImplementationTest {

    @Test
    void test() {
        Injector injector = InjectorFactory.createInjector(Sets.newHashSet("com.AMS.injection"), this);
        Service service = injector.getInstance(Service.class);
        Assertions.assertTrue(service instanceof MockedTestService, "Service should be instanceOf MockedTestService, but was " + service.getClass().getSimpleName());
    }
}
