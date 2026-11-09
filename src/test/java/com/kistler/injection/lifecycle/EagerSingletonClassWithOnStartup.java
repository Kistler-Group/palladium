package com.kistler.injection.lifecycle;

import com.kistler.injection.annotation.EagerSingleton;
import com.kistler.injection.lifecycle.startup.OnStartup;

@EagerSingleton
public class EagerSingletonClassWithOnStartup {
    private String content = "";

    @OnStartup
    public void onStartup() {
        content = ClassWithOnStartup.CHANGED_CONTENT;
    }

    public String getContent() {
        return content;
    }
}
