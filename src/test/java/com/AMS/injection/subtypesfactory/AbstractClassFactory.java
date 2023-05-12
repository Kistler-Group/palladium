package com.AMS.injection.subtypesfactory;

import com.AMS.injection.subtypefactory.SubTypesFactory;

public class AbstractClassFactory extends SubTypesFactory<String, AbstractClass> {
    public AbstractClassFactory() {
        super(AbstractClass.class, AbstractClass::getSupportedType);
    }
}
