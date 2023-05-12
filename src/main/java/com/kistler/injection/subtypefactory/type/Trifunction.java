package com.kistler.injection.subtypefactory.type;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Trifunction<A,B,C,R> {
    R apply(A a, B b, C c);

    default <V> Trifunction<A, B, C, V> andThen(
            Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c) -> after.apply(apply(a, b, c));
    }
}