package com.kistler.injection.subtypefactory;

import com.google.inject.Provider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A SubTypesFactory encapsulates the creation of components based on a certain key for that component. The key is
 * provided by the {@code Function<T, K> keyExtractor} as part of a {@code Collection<K>} that is used to connect the
 * implementation to multiple keys.
 * In addition to the functionality of MultiSubTypesFactory, it also checks the ancestors of a given type to check for implementations for an additional degree of freedom
 *
 * @param <K> Key for the component.
 * @param <T> Target implementation that should be used for the given key.
 */
public abstract class MultiSubTypesInheritableFactory<K, T> extends MultiSubTypesFactory<K, T> {
    private final Map<K, K> inheritance = new ConcurrentHashMap<>();

    protected MultiSubTypesInheritableFactory(Class<T> baseClass, Function<T, Collection<K>> keyExtractor) {
        super(baseClass, keyExtractor);
    }

    @Override
    public final Provider<? extends T> getProvider(K type) {
        type = inheritance.getOrDefault(type, type);
        Provider<? extends T> result = super.getProvider(type);
        if (result != null) {
            return result;
        }
        for (K ancestor : getAncestors(type)) {
            result = super.getProvider(ancestor);
            if (result != null) {
                inheritance.put(type, ancestor);
                return result;
            }
        }
        return null;
    }

    protected abstract Collection<K> getAncestors(K type);
}
