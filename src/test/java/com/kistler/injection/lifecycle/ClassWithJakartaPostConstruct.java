package com.kistler.injection.lifecycle;

import jakarta.annotation.PostConstruct;

public class ClassWithJakartaPostConstruct {
    public static final String CHANGED_CONTENT = "Jakarta PostConstruct Content";
    private String content = "";

    @PostConstruct
    public void validPostConstructMethod(){
        content = CHANGED_CONTENT;
    }

    @PostConstruct
    private void invalidPostConstructMethod(){
        content = CHANGED_CONTENT + CHANGED_CONTENT;
    }

    public String getContent() {
        return content;
    }
}
