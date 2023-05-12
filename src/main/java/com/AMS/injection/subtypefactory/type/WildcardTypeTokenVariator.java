package com.AMS.injection.subtypefactory.type;

import com.AMS.injection.subtypefactory.type.TypeTreeNode.Kind;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Wrapper to encapsulate a TypeToken that contains a wildcard, and gives access to the "resolved" TypeToken with the resolved wildcards
 * Extension of the thought behind TypeTreeNode
 */
public class WildcardTypeTokenVariator<T> {
	private final TypeTreeNode root;
	private final TypeTreeNode objectWildcard;

	public WildcardTypeTokenVariator(TypeToken<T> typeToken) {
		root = TypeTreeNode.of(typeToken.getType());
		objectWildcard = new TypeTreeNode(Kind.Wildcard, null);
		objectWildcard.getChildren().add(new TypeTreeNode(Kind.Class, Object.class));
	}

	public Set<TypeToken<?>> generateAllVariations() {
		Set<TypeToken<?>> result = new LinkedHashSet<>();

		Set<TypeTreeNode> variations = variate(root, false);
		for (TypeTreeNode variation : variations) {
			result.add(TypeToken.of(variation.getGenericType()));
		}

		return result;
	}

	private Set<TypeTreeNode> variate(TypeTreeNode node, boolean isEncapsulated) {
		switch (node.getKind()) {
			case TypeVariable:
			case Array:
				return Collections.singleton(node);
			case Class:
				if (isEncapsulated) {
					return ImmutableSet.of(node, objectWildcard);
				} else {
					return Collections.singleton(node);
				}
			case Wildcard:
				if (node.getChildren().size() == 1 && isEncapsulated) {
					return ImmutableSet.of(node.getChildren().get(0), objectWildcard);
				} else {
					return Collections.singleton(node);
				}
			case Parametrised:
				List<List<TypeTreeNode>> lists = Lists.cartesianProduct(Lists.transform(node.getChildren(), child -> ImmutableList.copyOf(variate(child, true))));
				Set<TypeTreeNode> result = new LinkedHashSet<>(lists.size());
				for (List<TypeTreeNode> list : lists) {
					TypeTreeNode variation = new TypeTreeNode(Kind.Parametrised, node.getRawType());
					variation.getChildren().addAll(list);
					result.add(variation);
				}

				if (isEncapsulated) {
					result.add(objectWildcard);
				}
				return result;
			default:
				throw new IllegalArgumentException();
		}
	}
}
