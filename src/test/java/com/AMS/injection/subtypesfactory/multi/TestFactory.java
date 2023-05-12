package com.AMS.injection.subtypesfactory.multi;

import com.AMS.injection.subtypefactory.MultiSubTypesFactory;

public class TestFactory extends MultiSubTypesFactory<String, Target> {
    public TestFactory() {
        super(Target.class, Target::getKeys);
    }
}
