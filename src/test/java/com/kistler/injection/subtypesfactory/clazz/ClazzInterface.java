package com.kistler.injection.subtypesfactory.clazz;

import com.kistler.injection.subtypefactory.SubTypeConstructable;

@SubTypeConstructable
public interface ClazzInterface {
    Class<?> getSupportedType();
}
