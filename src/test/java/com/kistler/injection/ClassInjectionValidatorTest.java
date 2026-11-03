package com.kistler.injection;

import com.example.prefix.ScannedService;
import com.example.prefix.ScannedServiceImpl;
import com.example.prefix.sub.SubService;
import com.example.prefix.sub.SubServiceImpl;
import com.example.prefixsibling.SiblingApi;
import com.example.prefixsibling.SiblingServiceImpl;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClassInjectionValidatorTest {

    private final ClassInjectionValidator validator =
            new ClassInjectionValidator(Sets.newHashSet("com.example.custom", "com.kistler.injection"));

    @Test
    void rejectsSiblingPackageWithSharedPrefix() {
        Assertions.assertFalse(validator.acceptsPackage("com.example.custommodule"));
        Assertions.assertFalse(validator.acceptsPackage("com.example.custommodule.Foo"));
        Assertions.assertFalse(validator.acceptsPackage("com.example.custommodule.Foo.class"));
        Assertions.assertFalse(validator.acceptsPackage("com.kistler.injectionfoo.Foo"));
    }

    @Test
    void acceptsOnlyPackageItselfAndSubpackages() {
        Assertions.assertTrue(validator.acceptsPackage("com.example.custom"));
        Assertions.assertTrue(validator.acceptsPackage("com.example.custom.Foo"));
        Assertions.assertTrue(validator.acceptsPackage("com.example.custom.Foo$Bar"));
        Assertions.assertTrue(validator.acceptsPackage("com.example.custom.sub.Foo"));
        Assertions.assertTrue(validator.acceptsPackage("com.example.custom.Foo.class"));
        Assertions.assertTrue(validator.acceptsPackage(ClassInjectionValidator.class.getName()));

        Assertions.assertFalse(validator.acceptsPackage("com.example"));
        // Reflections retries slash-separated paths with the dotted name.
        Assertions.assertFalse(validator.acceptsPackage("com/example/custom/Foo.class"));
    }

    @Test
    void normalizesConfiguredPackageNames() {
        for (String configured : new String[]{"com.example.custom.", "com/example/custom/", "/com/example/custom",
                "com\\example\\custom", " com.example.custom "}) {
            ClassInjectionValidator normalized = new ClassInjectionValidator(Sets.newHashSet(configured));
            Assertions.assertTrue(normalized.acceptsPackage("com.example.custom"), configured);
            Assertions.assertTrue(normalized.acceptsPackage("com.example.custom.Foo"), configured);
            Assertions.assertFalse(normalized.acceptsPackage("com.example.custommodule.Foo"), configured);
        }
    }

    @Test
    void separatorOnlyPackageMatchesNothing() {
        for (String configured : new String[]{".", "/", " "}) {
            ClassInjectionValidator separatorOnly = new ClassInjectionValidator(Sets.newHashSet(configured));
            Assertions.assertFalse(separatorOnly.acceptsPackage("com.example.Foo"), configured);
            Assertions.assertFalse(separatorOnly.isTopLevel(Comparable.class), configured);
        }
    }

    @Test
    void emptyPackageStillAcceptsEverything() {
        ClassInjectionValidator all = new ClassInjectionValidator(Sets.newHashSet(""));
        Assertions.assertTrue(all.acceptsPackage("com.example.custommodule.Foo"));
        Assertions.assertTrue(all.acceptsPackage("org/other/Bar.class"));
    }

    @Test
    void isTopLevelRespectsPackageBoundary() {
        ClassInjectionValidator prefix = new ClassInjectionValidator(Sets.newHashSet("com.example.prefix"));
        Assertions.assertFalse(prefix.isTopLevel(SiblingApi.class));
        Assertions.assertTrue(prefix.isTopLevel(ScannedService.class));
        Assertions.assertTrue(prefix.isTopLevel(SubService.class));
    }

    @Test
    void applyRespectsPackageBoundary() {
        ClassInjectionValidator prefix = new ClassInjectionValidator(Sets.newHashSet("com.example.prefix"));
        Assertions.assertFalse(prefix.apply(SiblingServiceImpl.class));
        Assertions.assertTrue(prefix.apply(ScannedServiceImpl.class));
        Assertions.assertTrue(prefix.apply(SubServiceImpl.class));
        Assertions.assertFalse(prefix.apply(ScannedService.class));
    }
}
