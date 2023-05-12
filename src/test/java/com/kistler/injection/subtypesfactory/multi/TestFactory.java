package com.kistler.injection.subtypesfactory.multi;

import com.kistler.injection.subtypefactory.MultiSubTypesFactory;

public class TestFactory extends MultiSubTypesFactory<String, Target> {
    public TestFactory() {
        super(Target.class, Target::getKeys);
    }
}
