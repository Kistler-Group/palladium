package com.kistler.injection.lifecycle;

import com.kistler.injection.annotation.EagerSingleton;
import com.kistler.injection.lifecycle.startup.OnStartup;

@EagerSingleton
public class EagerSingletonWithInterfaces implements FirstEagerInterface, SecondEagerInterface {
    @OnStartup
    public void onStartup() {
        // only the number of instances matters
    }
}
