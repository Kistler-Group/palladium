package com.kistler.injection;

import com.kistler.injection.annotation.EagerSingleton;
import com.kistler.injection.annotation.EnforceTestImplementationBinding;
import com.kistler.injection.annotation.ExcludeFromAutoBinding;
import com.kistler.injection.annotation.InjectorBootstrap;
import com.kistler.injection.subtypefactory.type.TypeTokenUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Prime module of the palladium project.
 *
 * Automatically checks all classes that either have a default constructor or a constructor annotated with @Inject for their interfaces
 * If there is a 1:1 relationship between interface and class, this module automatically binds the implementation.
 *
 * This enables very easy access to using Dependency Injection, in principle without creating a single individual module
 */
public class ReflectionsModule extends AbstractModule {
    private final Logger                  logger = LoggerFactory.getLogger(getClass());
    private final ClassInjectionValidator classInjectionValidator;
    private final Reflections             reflections;
    private final BootstrapModule         bootstrapModule;

    @InjectorBootstrap
    private ReflectionsModule(ClassInjectionValidator classInjectionValidator, Reflections reflections, BootstrapModule bootstrapModule) {
        this.classInjectionValidator = classInjectionValidator;
        this.reflections = reflections;
        this.bootstrapModule = bootstrapModule;
    }

    @Override
    public void configure() {
        Table<Key<?>, TypeLiteral<?>, Set<TypeToken<?>>> implementations = HashBasedTable.create();

        Set<Constructor> defaultConstructors = getClassesWithSingleDefaultConstructor();

        //retrieves all classes with an Injection- or Default-Constructor
        for (Constructor constructor : Sets.union(reflections.getConstructorsAnnotatedWith(Inject.class), defaultConstructors)) {
            Class<?> cls = constructor.getDeclaringClass();

            // Excludes classes with the ExcludeFromAutoBinding annotation
            // Excludes Non-constructable classes (interfaces, abstracts) as well
            if (hasNoExcludeAnnotation(cls)
                    && classInjectionValidator.apply(cls)
                    && classInjectionValidator.isSpecific(cls)) {
                Set<Annotation> annotations = getBindingAnnotationsForClass(cls);
                Set<TypeToken<?>> typeTokens = getNestedBindingTypes(cls);

                //Builds a keySet to include types and their tokens
                for (TypeToken<?> typeToken : typeTokens) {
                    for (Annotation annotation : annotations) {
                        implementations.put(TypeTokenUtils.toKey(typeToken, annotation), TypeLiteral.get(cls), typeTokens);
                    }
                }
            }
        }

        //Checks every found class
        int count = 0;
        for (Key<?> key : implementations.rowKeySet()) {
            Class<?> cls = key.getTypeLiteral().getRawType();

            //validates if the given class is a top level class
            if (classInjectionValidator.isTopLevel(cls)) {
                Map<TypeLiteral<?>, Set<TypeToken<?>>> candidates = implementations.row(key);
                TypeLiteral<?> target = select(candidates);
                if (target != null
                        && !bootstrapModule.isBootstrapped(TypeToken.of(target.getType()))
                        && (key.getAnnotation() != null || !key.getTypeLiteral().equals(target))
                        && hasNoExcludeAnnotation(cls)) {
                    logger.trace("Binding {} from {}.", target, key);

                    //Binds classes to their interfaces, checks for EagerSingleton
                    ScopedBindingBuilder bindingBuilder = bind(key).to((TypeLiteral) target);
                    if (cls.isAnnotationPresent(EagerSingleton.class) || (target.getRawType().isAnnotationPresent(EagerSingleton.class) && !key.getTypeLiteral().equals(target))) {
                        bindingBuilder.asEagerSingleton();
                    }
                    count++;
                }
            }
        }
        logger.info("Identified a total of {} bindings from {} possibilities.", count, implementations.size());
    }

    /**
     * Retrieves classes which *only* have a default constructor. If a secondary constructor is present, the default
     * constructor should not be used for DI, as it's solely meant for serialisation
     */
    private Set<Constructor> getClassesWithSingleDefaultConstructor() {
        Set<Constructor> defaultConstructors = new LinkedHashSet<>();
        Set<Constructor> emptyConstructors = reflections.getConstructorsMatchParams();
        for (Constructor constructor : emptyConstructors) {
            if (constructor.getDeclaringClass().getConstructors().length == 1) {
                defaultConstructors.add(constructor);
            }
        }
        return defaultConstructors;
    }

    /**
     * Excludes all classes that are excluded from auto binding. This is signified by {@link ExcludeFromAutoBinding}
     * annotation Note that one layer of annotations are also checked, meaning that if a class has an annotation which
     * itself is annotated with {@link ExcludeFromAutoBinding} it will also be excluded This can be used to create
     * custom annotations for other modules to automatically
     */
    private boolean hasNoExcludeAnnotation(Class<?> cls) {
        for (Annotation annotation : cls.getAnnotations()) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            if (annotationClass.isAnnotationPresent(ExcludeFromAutoBinding.class)) {
                return false;
            }
        }
        return !cls.isAnnotationPresent(ExcludeFromAutoBinding.class);
    }

    @Nullable
    private TypeLiteral<?> select(Map<TypeLiteral<?>, Set<TypeToken<?>>> candidates) {
        Iterator<Entry<TypeLiteral<?>, Set<TypeToken<?>>>> iterator = candidates.entrySet().iterator();
        Entry<TypeLiteral<?>, Set<TypeToken<?>>> first = iterator.next();

        TypeLiteral<?> result = first.getKey();
        TypeLiteral<?> explicitBinding = null;

        if (isExplicitBinding(result)) {
            explicitBinding = result;
        }

        Set<TypeToken<?>> minimalCommonSet = first.getValue();
        boolean isIntersection = false;

        while (iterator.hasNext()) {
            Entry<TypeLiteral<?>, Set<TypeToken<?>>> entry = iterator.next();

            if (isExplicitBinding(entry.getKey())) {
                if (explicitBinding != null) {
                    throw new IllegalStateException("Multiple implementations with @EnforceTestImplementationBinding are found.");
                } else {
                    explicitBinding = entry.getKey();
                }
            }

            if (minimalCommonSet.containsAll(entry.getValue())) {
                // found a new entry which is covered by all previous entries
                if (isIntersection || entry.getValue().size() < minimalCommonSet.size()) {
                    // actually smaller than the common set or the first common ancestor
                    result = entry.getKey();
                } else {
                    // it has the same size as the previous result, thus they are equivalent and a distinction is not possible
                    result = null;
                }
                minimalCommonSet = entry.getValue();
                isIntersection = false;
            } else if (!entry.getValue().containsAll(minimalCommonSet)) {
                // found an entry from a separate branch which don't have a relation, the current minimal entry is therefor invalid
                result = null;
                isIntersection = true;
                minimalCommonSet = Sets.intersection(minimalCommonSet, entry.getValue());
            }
        }

        return explicitBinding == null ? result : explicitBinding;
    }

    private boolean isExplicitBinding(TypeLiteral<?> typeLiteral) {
        // This will mark the implementation binding as enforced binding
        // noinspection TestOnlyProblems
        return typeLiteral.getRawType().isAnnotationPresent(EnforceTestImplementationBinding.class);
    }

    private Set<TypeToken<?>> getNestedBindingTypes(Class<?> cls) {
        Set<TypeToken<?>> result = new LinkedHashSet<>();
        for (TypeToken<?> typeToken : TypeToken.of(cls).getTypes()) {
            TypeToken<?> resolvedTypeToken = TypeTokenUtils.resolvedTypeToken(typeToken);
            result.addAll(TypeTokenUtils.wildcardVariations(resolvedTypeToken));
        }
        return result;
    }

    private Set<Annotation> getBindingAnnotationsForClass(Class<?> cls) {
        Set<Annotation> result = new HashSet<>();
        for (Annotation annotation : cls.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(BindingAnnotation.class)) {
                result.add(annotation);
            }
        }
        if (result.isEmpty()) {
            result.add(null);
        }
        return result;
    }
}
