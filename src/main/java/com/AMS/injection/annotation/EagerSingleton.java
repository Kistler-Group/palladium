package com.AMS.injection.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Note that EagerSingleton is currently only used by ReflectionsModule
 * <p>
 * This implies that classes that have no {@link com.google.inject.BindingAnnotation} and no Interface to which they
 * need to be bound are not treated as eager singletons
 * <p>
 * An alternative would be setting EagerSingleton itself as a Binding Annotation, but that would require filtering
 * interfaces in the ReflectionsModule to prevent them from binding onto themselves (since they inherit the
 * BindingAnnotation from their IMPL)
 */
//@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface EagerSingleton {
}
