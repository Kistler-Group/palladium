package com.AMS.injection.annotation;

import com.google.common.annotations.VisibleForTesting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DISCLAIMER: This annotation should only be used in the test scope, when it is necessary to bind mock implementations
 * for certain interfaces.
 * <p>
 * This indicates that the annotated implementation should be used for the implemented interface and all other
 * implementations should be ignored.
 * <p>
 * If this is used on more than one class in a type hierarchy, the injector will throw a {@link IllegalStateException}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@VisibleForTesting
public @interface EnforceTestImplementationBinding {
}
