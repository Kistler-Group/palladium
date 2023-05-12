package com.AMS.injection.subtypesfactory.clazz;

import com.AMS.injection.subtypefactory.SubTypesClassFactory;

public class ClazzFactory extends SubTypesClassFactory<ClazzInterface> {

    public ClazzFactory() {
        super(ClazzInterface.class, ClazzInterface::getSupportedType);
    }
}
