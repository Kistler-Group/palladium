package com.AMS.injection.subtypefactory.type;

import com.AMS.injection.subtypefactory.type.TypeTreeNode.Kind;
import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Function;

public class ResolvedTypeTokenBuilder<T> {
    private final TypeTreeNode root;

    private ResolvedTypeTokenBuilder(TypeTreeNode root) {
        this.root = root;
    }

    public ResolvedTypeTokenBuilder(TypeToken<T> typeToken) {
        this(TypeTreeNode.of(typeToken.getType()));
    }

    public TypeToken<?> toTypeToken() {
        return TypeToken.of(root.getGenericType());
    }

    public ResolvedTypeTokenBuilder<T> stripWildcards() {
        return new ResolvedTypeTokenBuilder<>(transform(root, Collections.emptyMap(), this::stripWildcard));
    }

    public ResolvedTypeTokenBuilder<T> expandTypeVariablesOnce() {
        return new ResolvedTypeTokenBuilder<>(transform(root, Collections.emptyMap(), this::expandTypeVariableOnce));
    }

    public ResolvedTypeTokenBuilder<T> replaceTypeVariablesWithObject() {
        return new ResolvedTypeTokenBuilder<>(transform(root, Collections.emptyMap(), this::replaceTypeVariableWithObject));
    }

    private TypeTreeNode stripWildcard(TypeTreeNode node, Map<Type, TypeTreeNode> context, Function<TypeTreeNode, TypeTreeNode> childTransform) {
        if (node.getKind() == Kind.Wildcard) {
            return stripWildcard(node.getChildren().get(0), context, childTransform);
        }
        return transformChildren(node, childTransform);
    }

    private TypeTreeNode expandTypeVariableOnce(TypeTreeNode node, Map<Type, TypeTreeNode> context, Function<TypeTreeNode, TypeTreeNode> childTransform) {
        switch (node.getKind()) {
            case Class:
                return transformChildren(node, new Function<TypeTreeNode, TypeTreeNode>() {
                    TypeVariable<? extends Class<?>>[] typeParameters = ((Class<?>) node.getRawType()).getTypeParameters();
                    int i = 0;

                    @Override
                    public TypeTreeNode apply(TypeTreeNode typeTreeNode) {
                        context.put(typeParameters[i++], typeTreeNode);
                        return childTransform.apply(typeTreeNode);
                    }
                });
            case TypeVariable:
                return context.getOrDefault(node.getRawType(), node);
            default:
                return transformChildren(node, childTransform);
        }
    }

    private TypeTreeNode replaceTypeVariableWithObject(TypeTreeNode node, Map<Type, TypeTreeNode> context, Function<TypeTreeNode, TypeTreeNode> childTransform) {
        if (node.getKind() == Kind.TypeVariable) {
            return new TypeTreeNode(Kind.Class, Object.class);
        }
        return transformChildren(node, childTransform);
    }

    private TypeTreeNode transform(TypeTreeNode node, Map<Type, TypeTreeNode> context, Trifunction<TypeTreeNode, Map<Type, TypeTreeNode>, Function<TypeTreeNode, TypeTreeNode>, TypeTreeNode> transformFunction) {
        Map<Type, TypeTreeNode> resultContext = node.getKind() == Kind.Class ? new HashMap<>(context) : context;
        return transformFunction.apply(node, resultContext, child -> transform(child, resultContext, transformFunction));
    }

    private TypeTreeNode transformChildren(TypeTreeNode node, Function<TypeTreeNode, TypeTreeNode> childTransform) {
        List<TypeTreeNode> transform = ImmutableList.copyOf(Lists.transform(node.getChildren(), childTransform::apply));
        if (node.getChildren().equals(transform)) {
            return node;
        }
        TypeTreeNode result = new TypeTreeNode(node.getKind(), node.getRawType());
        result.getChildren().addAll(transform);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("root", root).toString();
    }
}
