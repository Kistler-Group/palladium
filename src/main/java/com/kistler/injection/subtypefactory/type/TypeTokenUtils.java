package com.kistler.injection.subtypefactory.type;

import com.google.common.reflect.TypeToken;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Utility class to turn types into literals or keys or resolve them
 */
public class TypeTokenUtils {
    public static <T> TypeLiteral<T> convert(TypeToken<T> tt) {
        return (TypeLiteral<T>) TypeLiteral.get(tt.getType());
    }

    public static <T> Key<T> toKey(@Nonnull TypeToken<T> typeToken) {
        return Key.get(convert(typeToken));
    }

    public static <T> Key<T> toKey(@Nonnull TypeToken<T> typeToken, @Nullable Annotation annotation) {
        if (annotation == null) {
            return toKey(typeToken);
        }
        return Key.get(convert(typeToken), annotation);
    }

    public static <T> TypeToken<T> resolvedTypeToken(TypeToken<T> typeToken) {
        TypeToken<?> result = new ResolvedTypeTokenBuilder<>(typeToken)
                .stripWildcards()
                .expandTypeVariablesOnce()
                .replaceTypeVariablesWithObject()
                .toTypeToken();

        return (TypeToken<T>) result;
    }

    public static <T> Set<TypeToken<T>> wildcardVariations(TypeToken<T> typeToken) {
        Set<TypeToken<?>> typeTokens = new WildcardTypeTokenVariator<>(typeToken)
                .generateAllVariations();
        return (Set) typeTokens;
    }
}
