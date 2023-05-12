package com.AMS.injection.subtypefactory;

import com.google.common.collect.Collections2;
import com.google.common.reflect.TypeToken;

import java.util.Collection;
import java.util.function.Function;

/**
 * An extension of the SubTypesFactory that is capable of checking for alternative implementations for a given type.
 * If no implementation for the requested type is found, a virtual hierarchy of types is checked for other ancestors that can be used instead
 *
 * Always based on "Class" as requesting type and the class type hierarchy to check for ancestors.
 *
 * All SubTypesFactories encapsulate the creation of components based on a certain key for that component. The key is
 * provided by the {@code Function<T, K> keyExtractor}, which is used to connect the key to the implementation.
 *
 * @param <T> Target implementation that should be used for the given key.
 */
public abstract class SubTypesClassFactory<T> extends SubTypesInheritableFactory<Class<?>, T> {
	protected SubTypesClassFactory(Class<T> baseClass, Function<T, Class<?>> keyExtractor) {
		super(baseClass, keyExtractor);
	}

	@Override
	protected final Collection<Class<?>> getAncestors(Class<?> type) {
		return Collections2.transform(TypeToken.of(type).getTypes(), TypeToken::getRawType);
	}
}
