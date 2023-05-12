package com.AMS.injection.lifecycle;

import com.AMS.injection.lifecycle.startup.OnStartup;

public class ClassWithOnStartup {
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
