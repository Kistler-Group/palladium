package com.AMS.injection.subtypefactory;

import com.AMS.injection.ClassInjectionValidator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A SubTypesFactory encapsulates the creation of components based on a certain key for that component. The key is
 * provided by the {@code Function<T, K> keyExtractor}, which is used to connect the key to the implementation.
 *
 * @param <K> Key for the component.
 * @param <T> Target implementation that should be used for the given key.
 */
public abstract class SubTypesFactory<K, T> {
    private final Class<T>                      baseClass;
    private final Function<T, K>                keyExtractor;
    private       Map<K, Provider<? extends T>> providers;

    public SubTypesFactory(Class<T> baseClass, Function<T, K> keyExtractor) {
        Preconditions.checkArgument(baseClass.isAnnotationPresent(SubTypeConstructable.class), "Annotation @%s is missing at construction base interface %s.", SubTypeConstructable.class.getSimpleName(), baseClass);
        this.baseClass = baseClass;
        this.keyExtractor = keyExtractor;
    }

    @Inject
    protected void init(Injector injector, Reflections reflections, ClassInjectionValidator classInjectionValidator) {
        Map<K, Provider<? extends T>> builder = new HashMap<>();
        for (Class<? extends T> cls : reflections.getSubTypesOf(baseClass)) {
            if (classInjectionValidator.apply(cls)) {
                T t = makePrototype(cls, injector);
                K k = keyExtractor.apply(t);
                Provider<? extends T> previous = builder.put(k, injector.getProvider(cls));
                Preconditions.checkState(previous == null, "Two prototypes for %s found: %s, %s", k, previous, cls);
            }
        }
        providers = ImmutableMap.copyOf(builder);
    }

    protected T makePrototype(Class<? extends T> cls, Injector injector) {
        return injector.getInstance(cls);
    }

    public Set<K> getSupportedTypes() {
        return providers.keySet();
    }

    public boolean isSupported(K type) {
        return getProvider(type) != null;
    }

    @Nonnull
    public final T create(K type) {
        Provider<? extends T> p = getProvider(type);
        if (p == null) throw new ProviderNotFoundException(type);
        return p.get();
    }

    @Nullable
    public Provider<? extends T> getProvider(K type) {
        return providers.get(type);
    }
}