package com.kistler.injection.lifecycle.shutdown;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that is executed when {@code PalladiumInjector.shutdown()} is called.
 *
 * <p>The method must be <b>public</b> and must not be static; any other method is never executed (a warning is
 * logged). It is executed on
 * every instance the injector has created up to then. Shutdown never creates an instance, so a singleton that was never
 * used is skipped.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnShutdown {
    Class<?>[] after() default {};

    Class<?>[] before() default {};
}
