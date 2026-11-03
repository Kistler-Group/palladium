package com.kistler.injection.lifecycle;

import com.kistler.injection.lifecycle.shutdown.OnShutdown;
import com.kistler.injection.lifecycle.startup.OnStartup;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import static java.lang.reflect.Modifier.isPublic;

/**
 * Decorator to check methods for basic qualities, such as parameterisation or certain annotations (like PostConstruct)
 */
public enum MethodPredicate implements Predicate<Method> {
    /**
     * Matches both javax.annotation.PostConstruct and jakarta.annotation.PostConstruct. The annotations are compared by
     * name, so neither of them has to be on the classpath.
     */
    ANNOTATED_WITH_POSTCONSTRUCT {
        private final Set<String> postConstructAnnotations = ImmutableSet.of(
                "javax.annotation.PostConstruct",
                "jakarta.annotation.PostConstruct");

        @Override
        public boolean apply(final Method method) {
            for (final Annotation annotation : method.getAnnotations()) {
                if (postConstructAnnotations.contains(annotation.annotationType().getName())) {
                    return true;
                }
            }
            return false;
        }
    },
    VOID {
        @Override
        public boolean apply(final Method method) {
            return Void.TYPE.equals(method.getReturnType());
        }
    },
    IS_PARAMETERLESS {
        @Override
        public boolean apply(final Method method) {
            return method.getParameterTypes().length == 0;
        }
    },

    PUBLIC {
        @Override
        public boolean apply(final Method method) {
            return isPublic(method.getModifiers());
        }
    },
    VALID_POSTCONSTRUCT {
        @Override
        public boolean apply(final Method method) {
            return Predicates.and(ANNOTATED_WITH_POSTCONSTRUCT, VOID, IS_PARAMETERLESS, PUBLIC).apply(method);
        }
    },
    ANNOTATED_WITH_ONSHUTDOWN {
        @Override
        public boolean apply(final Method method) {
            return method.isAnnotationPresent(OnShutdown.class);
        }
    },
    ANNOTATED_WITH_ONSTARTUP {
        @Override
        public boolean apply(final Method method) {
            return method.isAnnotationPresent(OnStartup.class);
        }
    }
}
