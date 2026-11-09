package com.kistler.injection.lifecycle.startup;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.kistler.injection.annotation.EagerSingleton;
import com.kistler.injection.lifecycle.PhaseStrategy;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Strategy to enter the startup phase and invoke methods annotated with the {@link OnStartup} annotation
 */
@Singleton
public class StartupStrategy extends PhaseStrategy<OnStartup> {

    @Inject
    public StartupStrategy(Injector injector, Reflections reflections) {
        super(injector, reflections);
    }

    public void startup() {
        enterPhase(OnStartup.class);
    }

    /**
     * Up to 1.3.8, startup created the instance itself. Since then it only runs on instances that already exist, so a
     * singleton nobody injects before startup has to be an {@link EagerSingleton}. This is easy to miss, hence the
     * warning. Instances of other scopes may legitimately be created later.
     */
    @Override
    protected void reportMissingInstance(Class<OnStartup> phaseAnnotation, Method method) {
        List<String> missingSingletons = findMissingSingletons(method);
        if (missingSingletons.isEmpty()) {
            super.reportMissingInstance(phaseAnnotation, method);
        } else {
            logger.warn("{} is not executed for @{}, as the singleton {} has not been created yet. Annotate it with @EagerSingleton if it isn't injected before startup.",
                    method, phaseAnnotation.getSimpleName(), String.join(", ", missingSingletons));
        }
    }

    /**
     * The method may be inherited, so this checks every concrete class that has it rather than the declaring class.
     * An {@link EagerSingleton} without an instance has been excluded from binding on purpose.
     */
    private List<String> findMissingSingletons(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        Set<Class<?>> classes = new LinkedHashSet<>(reflections.getSubTypesOf(declaringClass));
        classes.add(declaringClass);
        return classes.stream()
                .filter(cls -> !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers()))
                .filter(cls -> !cls.isAnnotationPresent(EagerSingleton.class))
                .filter(this::isSingleton)
                .map(Class::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Only inspects existing bindings, as {@link Injector#getBinding} would create a just-in-time binding, which a
     * child injector could then no longer bind explicitly.
     */
    private boolean isSingleton(Class<?> cls) {
        Binding<?> binding = injector.getExistingBinding(Key.get(cls));
        if (binding != null) {
            return Scopes.isSingleton(binding);
        }
        return cls.isAnnotationPresent(Singleton.class) || cls.isAnnotationPresent(javax.inject.Singleton.class);
    }

    @Override
    protected Class<?>[] getPredecessors(OnStartup annotation) {
        return annotation.after();
    }

    @Override
    protected Class<?>[] getSuccessors(OnStartup annotation) {
        return annotation.before();
    }
}
