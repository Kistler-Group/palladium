package com.AMS.injection.subtypefactory.type;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.util.Types;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrapper to encapsulate a TypeToken so that the hierarchy of the actual type is represented
 * Grants easier access to the children types as well
 */
public class TypeTreeNode {
    private static final Map<Class<?>, String> NATIVE_STRINGS = ImmutableMap.<Class<?>, String>builder()
            .put(void.class, "void")
            .put(char.class, "char")
            .put(boolean.class, "boolean")
            .put(byte.class, "byte")
            .put(short.class, "short")
            .put(int.class, "int")
            .put(long.class, "long")
            .put(float.class, "float")
            .put(double.class, "double")
            .build();

    public enum Kind {
        Class,
        TypeVariable,
        Wildcard,
        Parametrised,
        Array
    }

    public enum Verbosity {
        FullClassName,
        TypeParameters,
        WildcardBounds
    }

    private final Kind               kind;
    private final Type               rawType;
    private final List<TypeTreeNode> children = new ArrayList<>();

    public static TypeTreeNode of(Type type) {
        TypeTreeNode result;
        if (type instanceof Class) {
            if (((Class) type).isArray()) {
                result = new TypeTreeNode(Kind.Array, null);
                result.getChildren().add(of(((Class) type).getComponentType()));
            } else {
                result = new TypeTreeNode(Kind.Class, type);
                result.getChildren().addAll(Arrays.stream(((Class) type).getTypeParameters()).map(tp -> of(tp.getBounds()[0])).collect(Collectors.toList()));
            }
        } else if (type instanceof ParameterizedType) {
            result = new TypeTreeNode(Kind.Parametrised, ((ParameterizedType) type).getRawType());
            Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
            TypeVariable<? extends Class<?>>[] typeParameters = rawType.getTypeParameters();
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualArgument = actualTypeArguments[i];
                // prefer the definition bound if no bound was given for the actual argument
                if (actualArgument instanceof WildcardType && Object.class.equals(((WildcardType) actualArgument).getUpperBounds()[0])) {
                    result.getChildren().add(of(Types.subtypeOf(typeParameters[i].getBounds()[0])));
                } else {
                    result.getChildren().add(of(actualArgument));
                }
            }
        } else if (type instanceof WildcardType) {
            result = new TypeTreeNode(Kind.Wildcard, null);
            result.getChildren().addAll(Arrays.stream(((WildcardType) type).getUpperBounds()).map(TypeTreeNode::of).collect(Collectors.toList()));
        } else if (type instanceof TypeVariable) {
            result = new TypeTreeNode(Kind.TypeVariable, type);
        } else if (type instanceof GenericArrayType) {
            result = new TypeTreeNode(Kind.Array, null);
            result.getChildren().add(of(((GenericArrayType) type).getGenericComponentType()));
        } else {
            throw new IllegalArgumentException("Unsupported type provided " + type);
        }
        return result;
    }

    TypeTreeNode(Kind kind, Type rawType) {
        this.kind = kind;
        this.rawType = rawType;
    }

    public Kind getKind() {
        return kind;
    }

    public Type getRawType() {
        return rawType;
    }

    public Type getGenericType() {
        switch (kind) {
            case Class: {
                if (children.isEmpty()) {
                    return rawType;
                }
                Type[] types = Lists.transform(children, TypeTreeNode::getGenericType).toArray(new Type[children.size()]);
                return Types.newParameterizedType(rawType, types);
            }
            case TypeVariable: {
                return rawType;
            }
            case Wildcard: {
                return Types.subtypeOf(children.get(0).getGenericType());
            }
            case Parametrised: {
                Type[] types = Lists.transform(children, TypeTreeNode::getGenericType).toArray(new Type[children.size()]);
                if (((Class<?>) rawType).getEnclosingClass() == null) {
                    return Types.newParameterizedType(rawType, types);
                } else {
                    return Types.newParameterizedTypeWithOwner(((Class<?>) rawType).getEnclosingClass(), rawType, types);
                }
            }
            case Array: {
                return Types.arrayOf(children.get(0).getGenericType());
            }
        }
        throw new IllegalStateException("Unreachable state.");
    }

    public List<TypeTreeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return toString(EnumSet.allOf(Verbosity.class));
    }

    public String toString(Set<Verbosity> verbosity) {
        if (NATIVE_STRINGS.containsKey(rawType)) {
            return NATIVE_STRINGS.get(rawType);
        }
        switch (kind) {
            case Class:
            case Parametrised:
                Class<?> cls = (Class<?>) this.rawType;
                String name = verbosity.contains(Verbosity.FullClassName) ? cls.getCanonicalName() : cls.getSimpleName();
                if (children.isEmpty() || !verbosity.contains(Verbosity.TypeParameters)) {
                    return name;
                }
                return name + "<" + Joiner.on(", ").join(Lists.transform(children, c -> c.toString(verbosity))) + ">";
            case TypeVariable:
                return this.rawType.getTypeName();
            case Wildcard:
                return verbosity.contains(Verbosity.WildcardBounds) ? "? extends " + children.get(0) : "?";
            case Array:
                return children.get(0).toString(verbosity) + "[]";
        }
        return null;
    }
}
