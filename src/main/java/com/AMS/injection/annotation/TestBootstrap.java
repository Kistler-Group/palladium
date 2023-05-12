package com.AMS.injection.annotation;

import com.AMS.injection.InjectorFactory;

import java.lang.annotation.*;

/**
 * Annotation to be used to denote Modules that are bootstrapped in test scopes
 * In such cases, it complements or  replaces {@link InjectorBootstrap} as the base annotation for the {@link InjectorFactory}
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestBootstrap {
}
