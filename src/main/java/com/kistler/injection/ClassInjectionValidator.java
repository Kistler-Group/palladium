package com.kistler.injection;

import com.google.common.base.CharMatcher;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Helper class to check if a given class is constructable and is not an interface/is top level.
 */
public class ClassInjectionValidator {
	private static final CharMatcher PATH_SEPARATORS = CharMatcher.anyOf("/\\");
	private static final CharMatcher DOTS = CharMatcher.is('.');

	private final List<String> packageNames = new ArrayList<>();
	private final boolean acceptsAllPackages;

	public ClassInjectionValidator(Set<String> packageNames) {
		for (String packageName : packageNames) {
			String name = normalize(packageName);
			// A name made up only of separators (e.g. ".") names no package and matches nothing.
			if (!name.isEmpty()) {
				this.packageNames.add(name);
			}
		}
		this.acceptsAllPackages = packageNames.contains("");
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

	/**
	 * Checks if the given dot-separated name (package, fully qualified class name or resource such as
	 * {@code com.kistler.injection.Foo.class}) lies within one of the configured packages, or if the empty package is
	 * configured, which accepts everything. Reflections' input filter first passes the slash-separated path, which only
	 * the empty package accepts, and then retries with the dotted name.
	 */
	public boolean acceptsPackage(String packageName) {
		if (acceptsAllPackages) {
			return true;
		}
		for (String name : packageNames) {
			// Require a package boundary so that "com.example.custom" doesn't accept "com.example.custommodule".
			if (packageName.startsWith(name)
					&& (packageName.length() == name.length() || packageName.charAt(name.length()) == '.')) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Turns {@code com.example}, {@code com/example/}, {@code /com/example} or {@code com\example} into
	 * {@code com.example}.
	 */
	private static String normalize(String packageName) {
		return DOTS.trimFrom(PATH_SEPARATORS.replaceFrom(packageName.trim(), '.'));
	}
}
