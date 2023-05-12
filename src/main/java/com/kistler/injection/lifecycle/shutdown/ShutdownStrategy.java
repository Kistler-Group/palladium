package com.kistler.injection.lifecycle.shutdown;

import com.kistler.injection.lifecycle.PhaseStrategy;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.reflections.Reflections;

/**
 * Strategy to enter the shutdown phase and invoke methods annotated with the {@link OnShutdown} annotation
 */
@Singleton
public class ShutdownStrategy extends PhaseStrategy<OnShutdown> {
    @Inject
    public ShutdownStrategy(Injector injector, Reflections reflections) {
        super(injector, reflections);
    }

    public void shutdown() {
        enterPhase(OnShutdown.class);
    }

    @Override
    protected Class<?>[] getPredecessors(OnShutdown annotation) {
        return annotation.after();
    }

    @Override
    protected Class<?>[] getSuccessors(OnShutdown annotation) {
        return annotation.before();
    }
}
