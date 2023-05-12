package com.AMS.injection;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Helper class to check if a given class is constructable and is not an interface/is top level.
 */
public class ClassInjectionValidator {
	private final Set<String> packageNames;

	public ClassInjectionValidator(Set<String> packageNames) {
		this.packageNames = packageNames;
	}

	public boolean apply(Class<?> cls) {
		return isConstructable(cls) && isTopLevel(cls);
	}

	public boolean isSpecific(Class<?> cls) {
		return cls.getTypeParameters().length == 0;
	}

	public boolean isTopLevel(Class<?> cls) {
		return cls.getEnclosingClass() == null &&
				acceptsPackage(cls.getName());
	}

	public boolean isConstructable(Class<?> cls) {
		return !Modifier.isAbstract(cls.getModifiers()) &&
				!Modifier.isInterface(cls.getModifiers());
	}

	public boolean acceptsPackage(String packageName) {
		for (String name : packageNames) {
			if (packageName.startsWith(name)) {
				return true;
			}
		}
		return false;
	}
}
