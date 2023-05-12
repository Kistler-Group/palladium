package com.kistler.injection.lifecycle;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract base class for executing phase-related methods, indicated by base annotations E.g. "All methods that should
 * be done before the system shuts down" use the ShutdownPhase, indicated by the OnShutDown annotation Basically
 * presents a hook that can be called to start a global phase and execute the associated methods
 */
public abstract class PhaseStrategy<T extends Annotation> {
    private final Logger      logger = LoggerFactory.getLogger(getClass());
    private final Injector    injector;
    private final Reflections reflections;
    private final Method      initialPredecessor;

    @Inject
    public PhaseStrategy(Injector injector, Reflections reflections) {
        this.injector = injector;
        this.reflections = reflections;
        try {
            initialPredecessor = PhaseStrategy.class.getDeclaredMethod("enterPhase", Class.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract Class<?>[] getPredecessors(T annotation);

    protected abstract Class<?>[] getSuccessors(T annotation);

    /**
     * Entrypoint to invoking all methods related to the phase
     *
     * @param phaseAnnotation Annotation for the specified phase
     */
    public void enterPhase(Class<T> phaseAnnotation) {
        Multimap<Class<?>, Method> methods = findPhaseMethods(phaseAnnotation);
        Multimap<Method, Method> predecessors = identifyPredecessors(phaseAnnotation, methods);
        Collection<Method> sortedMethods = sortMethods(phaseAnnotation, predecessors);
        invokeMethods(phaseAnnotation, sortedMethods);
    }

    /**
     * Reflections-based collection of all annotated methods
     *
     * @param phaseAnnotation Annotation for the specified phase
     */
    private Multimap<Class<?>, Method> findPhaseMethods(Class<T> phaseAnnotation) {
        Multimap<Class<?>, Method> result = HashMultimap.create();
        for (Method method : reflections.getMethodsAnnotatedWith(phaseAnnotation)) {
            result.put(method.getDeclaringClass(), method);
        }
        return result;
    }

    /**
     * Helper method to identify sequences within the methods that should be invoked in a given phase by retrieving
     * predecessors or a given collection of methods
     */
    private Multimap<Method, Method> identifyPredecessors(Class<T> phaseAnnotation, final Multimap<Class<?>, Method> methods) {
        LoadingCache<Class<?>, Collection<Method>> cache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Collection<Method>>() {
            @Override
            public Collection<Method> load(Class<?> key) throws Exception {
                Set<Method> result = new HashSet<Method>();
                for (Class<?> cls : methods.keySet()) {
                    if (key.isAssignableFrom(cls)) {
                        result.addAll(methods.get(cls));
                    }
                }
                return result;
            }
        });

        Multimap<Method, Method> result = HashMultimap.create();
        for (Method method : methods.values()) {
            result.put(method, initialPredecessor);
            T annotation = method.getAnnotation(phaseAnnotation);
            for (Class<?> cls : getPredecessors(annotation)) {
                result.putAll(method, cache.getUnchecked(cls));
            }
            for (Class<?> cls : getSuccessors(annotation)) {
                for (Method successor : cache.getUnchecked(cls)) {
                    result.put(successor, method);
                }
            }
        }
        return result;
    }

    /**
     * Sorts methods that should be invoked in the phase to ensure that potentially important sequences are kept
     */
    private Collection<Method> sortMethods(Class<T> phaseAnnotation, Multimap<Method, Method> predecessors) {
        Set<Method> result = new LinkedHashSet<Method>();
        result.add(initialPredecessor);
        while (result.size() <= predecessors.keySet().size()) {
            final int iterationSize = result.size();

            for (Method method : predecessors.keySet()) {
                if (result.containsAll(predecessors.get(method))) {
                    result.add(method);
                }
            }

            Preconditions.checkState(iterationSize < result.size(), "Found a cycle when ordering %s methods. The remaining ones are %s.", phaseAnnotation, Sets.difference(predecessors.keySet(), result));
        }
        result.remove(initialPredecessor);
        return result;
    }

    /**
     * Trigger of all collected & sorted methods
     */
    private void invokeMethods(Class<T> phaseAnnotation, Collection<Method> sortedMethods) {
        InstanceRegistry instanceRegistry = injector.getInstance(InstanceRegistry.class);
        for (Method method : sortedMethods) {
            if (!Modifier.isStatic(method.getModifiers())) {
                List<Object> methodTargets = instanceRegistry.getMethodTargets(phaseAnnotation, method);

                for (Object methodTarget : methodTargets) {
                    method.setAccessible(true);
                    try {
                        logger.info("Entring phase {}. Executing {}...", phaseAnnotation, method);
                        method.invoke(methodTarget);
                    } catch (IllegalAccessException e) {
                        logger.error("Error while entering phase {}.", phaseAnnotation, e);
                    } catch (InvocationTargetException e) {
                        logger.error("Error while entering phase {}.", phaseAnnotation, e);
                    } finally {
                        method.setAccessible(false);
                    }
                }
            }
        }
    }
}
