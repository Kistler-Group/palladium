package com.kistler.injection.lifecycle;

import com.kistler.injection.lifecycle.shutdown.OnShutdown;

public class ClassWithOnShutdown {
    public static final String CHANGED_CONTENT = "OnShutdown Content";

    private String content = "";

    @OnShutdown
    public void onShutdown() {
        content = CHANGED_CONTENT;
    }

    public String getContent() {
        return content;
    }
}
