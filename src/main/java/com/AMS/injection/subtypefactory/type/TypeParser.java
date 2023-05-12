package com.AMS.injection.subtypefactory.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.util.Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses Strings and Patterns to create reflections Types
 */
public class TypeParser {
	private interface MatchHandler {
		Type handle(MatchResult match) throws Exception;
	}

	private static final Map<Pattern, MatchHandler> TYPE_MATCHERS = ImmutableMap.<Pattern, MatchHandler>builder()
			// native types
			.put(Pattern.compile("void"), m -> void.class)
			.put(Pattern.compile("char"), m -> char.class)
			.put(Pattern.compile("boolean"), m -> boolean.class)
			.put(Pattern.compile("byte"), m -> byte.class)
			.put(Pattern.compile("short"), m -> short.class)
			.put(Pattern.compile("int"), m -> int.class)
			.put(Pattern.compile("long"), m -> long.class)
			.put(Pattern.compile("float"), m -> float.class)
			.put(Pattern.compile("double"), m -> double.class)

			// array
			.put(Pattern.compile("(.*)\\[\\]"), m -> Types.arrayOf(parse(m.group(1))))

			// parametrized type
			.put(Pattern.compile("(.*)<(.*)>"), m -> getParameterizedType(m))

			// wildcard lower bound
			.put(Pattern.compile("\\? *extends *(.*)"), m -> Types.subtypeOf(parse(m.group(1))))
			// wildcard upper bound
			.put(Pattern.compile("\\? *super *(.*)"), m -> Types.supertypeOf(parse(m.group(1))))
			// wildcard type
			.put(Pattern.compile("\\?"), m -> Types.subtypeOf(Object.class))

			// simple class name
			.put(Pattern.compile("[a-zA-Z$][a-zA-Z$0-9]*(\\.[a-zA-Z$][a-zA-Z$0-9]*)*"), m -> Class.forName(m.group()))

			.build();

	public static Type parse(String type) throws TypeParseException {
		try {
			type = type.trim();
			for (Entry<Pattern, MatchHandler> entry : TYPE_MATCHERS.entrySet()) {
				Matcher matcher = entry.getKey().matcher(type);
				if (matcher.matches()) {
					return entry.getValue().handle(matcher);
				}
			}
		} catch (Exception e) {
			throw new TypeParseException("Exception parsing type \"" + type + "\".", e);
		}
		throw new TypeParseException("Unable to parse \"" + type + "\".");
	}

	private static ParameterizedType getParameterizedType(MatchResult matchResult) throws TypeParseException {
		Type rawType = parse(matchResult.group(1));
		if (rawType instanceof Class && ((Class) rawType).getEnclosingClass() != null) {
			return Types.newParameterizedTypeWithOwner(((Class) rawType).getEnclosingClass(), rawType, parseList(matchResult.group(2)));
		} else {
			return Types.newParameterizedType(rawType, parseList(matchResult.group(2)));
		}
	}

	/**
	 * Extracts the strings of the types in a given List and then parses them
	 */
	private static Type[] parseList(String typeList) throws TypeParseException {
		List<Type> result = new ArrayList<>();
		int balance = 0;
		int lastpos = 0;

		char[] charArray = typeList.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			switch (c) {
				case '<':
					balance++;
					break;
				case '>':
					balance--;
					Preconditions.checkState(balance >= 0);
					break;
				case ',':
					if (balance == 0) {
						result.add(parse(typeList.substring(lastpos, i)));
						lastpos = i + 1;
					}
					break;
				default:
					break;
			}
		}
		Preconditions.checkState(balance == 0);
		result.add(parse(typeList.substring(lastpos)));
		return result.toArray(new Type[result.size()]);
	}
}
