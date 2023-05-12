package com.AMS.injection.override;

import com.AMS.injection.annotation.EnforceTestImplementationBinding;
import com.google.inject.Singleton;

@Singleton
@EnforceTestImplementationBinding
public class MockedTestService extends DefaultService {

    @Override
    public void doStuff() {
        // do something different
    }
}
