package com.kistler.injection.lifecycle.startup;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that is executed when {@code PalladiumInjector.startup()} is called.
 *
 * <p>The method must be <b>public</b> and must not be static; any other method is never executed (a warning is
 * logged). It is executed on
 * every instance the injector has created up to then; startup doesn't create instances itself. A class that isn't
 * injected anywhere therefore has to be annotated with {@link com.kistler.injection.annotation.EagerSingleton}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnStartup {
    Class<?>[] after() default {};

    Class<?>[] before() default {};
}
