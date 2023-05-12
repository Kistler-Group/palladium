package com.kistler.injection.subtypefactory;

import com.google.inject.Provider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * An extension of the SubTypesFactory that is capable of checking for alternative implementations for a given type.
 * If no implementation for the requested type is found, a virtual hierarchy of types is checked for other ancestors that can be used instead
 *
 * All SubTypesFactories encapsulate the creation of components based on a certain key for that component. The key is
 * provided by the {@code Function<T, K> keyExtractor}, which is used to connect the key to the implementation.
 *
 * @param <K> Key for the component.
 * @param <T> Target implementation that should be used for the given key.
 */
public abstract class SubTypesInheritableFactory<K, T> extends SubTypesFactory<K, T> {
	private final Map<K, K> inheritance = new ConcurrentHashMap<>();

	protected SubTypesInheritableFactory(Class<T> baseClass, Function<T, K> keyExtractor) {
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
