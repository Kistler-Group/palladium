package com.kistler.injection.lifecycle.startup;

import com.kistler.injection.lifecycle.PhaseStrategy;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.reflections.Reflections;

/**
 * Strategy to enter the startup phase and invoke methods annotated with the {@link OnStartup} annotation
 */
@Singleton
public class StartupStrategy extends PhaseStrategy<OnStartup> {
    @Inject
    public StartupStrategy(Injector injector, Reflections reflections) {
        super(injector, reflections);
    }

    public void startup() {
        enterPhase(OnStartup.class);
    }

    @Override
    protected Class<?>[] getPredecessors(OnStartup annotation) {
        return annotation.after();
    }

    @Override
    protected Class<?>[] getSuccessors(OnStartup annotation) {
        return annotation.before();
    }
}
