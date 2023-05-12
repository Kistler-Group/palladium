package com.kistler.injection.annotation;

import com.kistler.injection.ReflectionsModule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Interface that marks interfaces which should not be automatically bound to their respective implementations.
 * Used by the {@link ReflectionsModule}
 * The main use case this is intended for is creating specific modules for the annotated interface.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeFromAutoBinding {
}
