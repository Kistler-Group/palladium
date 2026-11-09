package com.kistler.injection.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a class that is created together with the injector and kept as a singleton.
 *
 * <p>Note that EagerSingleton is currently only used by ReflectionsModule. It binds the class itself as eager singleton
 * and binds every interface to the class as eager singleton as well, so all of them share one instance. This also
 * applies to a class without any interface, e.g. so that its {@code @OnStartup} methods run although nothing injects it.
 * <p>
 * A module may repeat such a binding exactly, e.g. {@code bind(Service.class).to(ServiceImpl.class).asEagerSingleton()},
 * as Guice merges identical bindings. Any other binding of the class or of one of its interfaces, e.g. with a different
 * scope or to a different target, is rejected by Guice when the injector is created. Bind such a class in the module
 * only and remove the annotation, or mark it with {@link ExcludeFromAutoBinding}.
 * <p>
 * An alternative would be setting EagerSingleton itself as a Binding Annotation, but that would require filtering
 * interfaces in the ReflectionsModule to prevent them from binding onto themselves (since they inherit the
 * BindingAnnotation from their IMPL)
 */
//@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface EagerSingleton {
}
