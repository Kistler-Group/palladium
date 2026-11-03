package com.kistler.injection.lifecycle;

public class ClassWithBothPostConstructs {
    private int calls = 0;

    @javax.annotation.PostConstruct
    @jakarta.annotation.PostConstruct
    public void postConstructMethod(){
        calls++;
    }

    public int getCalls() {
        return calls;
    }
}
