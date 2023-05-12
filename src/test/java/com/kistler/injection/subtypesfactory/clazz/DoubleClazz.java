package com.kistler.injection.subtypesfactory.clazz;

public class DoubleClazz implements ClazzInterface {
    @Override
    public Class<?> getSupportedType() {
        return Double.class;
    }
}
