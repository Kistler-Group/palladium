package com.kistler.injection.subtypesfactory.clazz;

import com.kistler.injection.subtypefactory.SubTypesClassFactory;

public class ClazzFactory extends SubTypesClassFactory<ClazzInterface> {

    public ClazzFactory() {
        super(ClazzInterface.class, ClazzInterface::getSupportedType);
    }
}
