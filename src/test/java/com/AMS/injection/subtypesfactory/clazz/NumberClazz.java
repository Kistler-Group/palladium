package com.AMS.injection.subtypesfactory.clazz;

public class NumberClazz implements ClazzInterface {
    @Override
    public Class<?> getSupportedType() {
        return Number.class;
    }
}
