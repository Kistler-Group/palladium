package com.kistler.injection;

import com.kistler.injection.annotation.InjectorBootstrap;
import com.kistler.injection.injector.PalladiumInjectorModule;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InjectorFactory {
    private static final Logger logger = LoggerFactory.getLogger(InjectorFactory.class);

    public static Injector createSpecificInjector(Set<String> packageNames, Class<? extends Annotation> baseAnnotation, Object... singletons) {
        Set<String> completePackageNames = new HashSet<>(packageNames);
        completePackageNames.add("com.AMS.injection");

        final ClassInjectionValidator classInjectionValidator = new ClassInjectionValidator(completePackageNames);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false),
                        new TypeAnnotationsScanner(),
                        new MethodAnnotationsScanner(),
                        new ResourcesScanner(),
                        new MethodParameterScanner(),
                        new FieldAnnotationsScanner())
                .forPackages(completePackageNames.toArray(new String[0]))
                .filterInputsBy(classInjectionValidator::acceptsPackage)
                .setExecutorService(new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(),
                        1, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(),
                        new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Reflection Scanner %d").build()));

        Reflections reflections = new Reflections(configurationBuilder);

        BootstrapModule bootstrapModule = new BootstrapModule(
                ImmutableSet.builder()
                        .add(classInjectionValidator, reflections)
                        .addAll(Arrays.asList(singletons))
                        .build());

        PalladiumInjectorModule palladiumInjectorModule = new PalladiumInjectorModule(reflections);

        List<Module> modules = Lists.newArrayList(bootstrapModule, palladiumInjectorModule);

        for (Constructor<?> constructor : reflections.getConstructorsAnnotatedWith(baseAnnotation)) {
            Preconditions.checkState(Module.class.isAssignableFrom(constructor.getDeclaringClass()), "@%s can only be used on implementations of guice modules.", InjectorBootstrap.class);
            if (classInjectionValidator.apply(constructor.getDeclaringClass())) {
                logger.info("Loading module {}.", constructor.getDeclaringClass());

                Type[] parameterTypes = constructor.getGenericParameterTypes();
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = bootstrapModule.retrieve(parameterTypes[i]);
                }

                try {
                    constructor.setAccessible(true);
                    modules.add((Module) constructor.newInstance(parameters));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.error("Can't create module {}.", constructor.getDeclaringClass(), e);
                }
            }
        }

        return Guice.createInjector(modules);
    }

    public static Injector createInjector(Set<String> packageNames, Object... singletons) {
        return createSpecificInjector(packageNames, InjectorBootstrap.class, singletons);
    }
}
