package com.kistler.injection;

import com.kistler.injection.subtypefactory.type.TypeTokenUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Filled with Bootstrap classes to avoid binding them again in other modules
 * Bootstrap classes have to be bound explicitly to their respective type token
 */
public class BootstrapModule extends AbstractModule {
	private final Map<TypeToken<?>, Object> mappings = new HashMap<>();

	public BootstrapModule(Object... bootstrapSingletons) {
		this(ImmutableSet.copyOf(bootstrapSingletons));
	}

	public BootstrapModule(Set<Object> bootstrapSingletons) {
		mappings.put(TypeToken.of(BootstrapModule.class), this);
		for (Object singleton : bootstrapSingletons) {
			for (TypeToken<?> type : TypeToken.of(singleton.getClass()).getTypes()) {
				if (mappings.containsKey(type)) {
					mappings.put(type, null);
				} else {
					mappings.put(type, singleton);
				}
			}
		}
	}

	public boolean isBootstrapped(TypeToken<?> type) {
		return mappings.get(type) != null;
	}

	public <T> T retrieve(Type instanceType) {
		return (T) mappings.get(TypeToken.of(instanceType));
	}

	@Override
	protected void configure() {
		for (Entry<TypeToken<?>, Object> entry : mappings.entrySet()) {
			if (entry.getValue() != null) {
				bind(TypeTokenUtils.toKey((TypeToken) entry.getKey())).toInstance(entry.getValue());
			}
		}
	}
}
