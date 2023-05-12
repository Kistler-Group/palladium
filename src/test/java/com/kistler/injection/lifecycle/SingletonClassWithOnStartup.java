package com.kistler.injection.lifecycle;

import com.kistler.injection.lifecycle.startup.OnStartup;
import com.google.inject.Singleton;

@Singleton
public class SingletonClassWithOnStartup {
    public static final String CHANGED_CONTENT = "OnStartup Content";

    private String content = "";

    @OnStartup
    public void onStartup() {
        content = CHANGED_CONTENT;
    }

    public String getContent() {
        return content;
    }
}
