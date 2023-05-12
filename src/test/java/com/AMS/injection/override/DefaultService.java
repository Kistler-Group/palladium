package com.AMS.injection.override;

import com.google.inject.Singleton;

@Singleton
public class DefaultService implements Service {

    @Override
    public void doStuff() {
        // do something
    }
}
