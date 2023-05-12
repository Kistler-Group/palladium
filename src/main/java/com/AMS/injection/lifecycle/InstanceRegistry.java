package com.AMS.injection.lifecycle;

import com.AMS.injection.annotation.ExcludeFromAutoBinding;
import com.google.common.collect.ImmutableList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ExcludeFromAutoBinding
public class InstanceRegistry {
    private final Map<Class<? extends Annotation>, Map<Method, ConcurrentLinkedQueue<Object>>> instances = new ConcurrentHashMap<>();

    <T> void add(Class<? extends Annotation> annotation, T injectee, Method method) {
        instances.computeIfAbsent(annotation, k -> new ConcurrentHashMap<>()).computeIfAbsent(method, m -> new ConcurrentLinkedQueue<>()).add(injectee);
    }

    List<Object> getMethodTargets(Class<? extends Annotation> annotation, Method method) {
        ConcurrentLinkedQueue<Object> elements = instances.get(annotation).get(method);
        if (elements == null) {
            return Collections.emptyList();
        } else {
            return ImmutableList.copyOf(elements);
        }
    }
}
