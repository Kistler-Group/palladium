package com.kistler.injection.subtypesfactory;

import com.kistler.injection.subtypefactory.SubTypesFactory;

public class AbstractClassFactory extends SubTypesFactory<String, AbstractClass> {
    public AbstractClassFactory() {
        super(AbstractClass.class, AbstractClass::getSupportedType);
    }
}
