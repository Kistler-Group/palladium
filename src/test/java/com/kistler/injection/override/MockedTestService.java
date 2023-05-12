package com.kistler.injection.override;

import com.kistler.injection.annotation.EnforceTestImplementationBinding;
import com.google.inject.Singleton;

@Singleton
@EnforceTestImplementationBinding
public class MockedTestService extends DefaultService {

    @Override
    public void doStuff() {
        // do something different
    }
}
