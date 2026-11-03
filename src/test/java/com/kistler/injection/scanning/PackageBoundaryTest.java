package com.kistler.injection.scanning;

import com.example.prefix.ScannedService;
import com.example.prefix.ScannedServiceImpl;
import com.example.prefix.sub.SubService;
import com.example.prefix.sub.SubServiceImpl;
import com.example.prefixsibling.SiblingApi;
import com.example.prefixsibling.SiblingService;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.kistler.injection.InjectorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

class PackageBoundaryTest {

    @Test
    void doesNotScanSiblingPackageWithSharedPrefix() {
        Injector injector = InjectorFactory.createInjector(Sets.newHashSet("com.example.prefix"));

        Assertions.assertTrue(injector.getInstance(ScannedService.class) instanceof ScannedServiceImpl);
        Assertions.assertNull(injector.getExistingBinding(Key.get(SiblingService.class)),
                "SiblingService from com.example.prefixsibling should not be bound");
        Assertions.assertTrue(injector.getInstance(Reflections.class).getSubTypesOf(SiblingService.class).isEmpty(),
                "com.example.prefixsibling should not be scanned");
    }

    @Test
    void doesNotBindSiblingPackageInterfaceOfScannedClass() {
        Injector injector = InjectorFactory.createInjector(Sets.newHashSet("com.example.prefix"));

        Assertions.assertNull(injector.getExistingBinding(Key.get(SiblingApi.class)),
                "SiblingApi from com.example.prefixsibling should not be bound");
    }

    @Test
    void scansSubpackages() {
        Injector injector = InjectorFactory.createInjector(Sets.newHashSet("com.example.prefix"));

        Assertions.assertTrue(injector.getInstance(SubService.class) instanceof SubServiceImpl);
    }
}
