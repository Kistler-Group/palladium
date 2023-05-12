package com.kistler.injection.annotation;

import java.lang.annotation.*;

/**
 * Annotation to denote modules that should automatically be loaded on the startup of the InjectorFactory.
 * Annotation has to be added to the constructor of the target module.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectorBootstrap {
}
