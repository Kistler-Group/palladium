package com.kistler.eageroverride;

import com.kistler.injection.annotation.EnforceTestImplementationBinding;

/**
 * Not annotated itself, as {@link com.kistler.injection.annotation.EagerSingleton} isn't inherited.
 */
@EnforceTestImplementationBinding
public class TestEagerService extends DefaultEagerService {
}
