package com.kistler.injection.lifecycle;

import javax.annotation.PostConstruct;

public class ClassWithPostConstruct {
    public static final String CHANGED_CONTENT = "PostConstruct Content";
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
