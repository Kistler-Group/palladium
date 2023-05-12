package com.kistler.injection.subtypesfactory.multi;

import java.util.Arrays;
import java.util.Collection;

public class DefaultTarget implements Target {
    @Override
    public Collection<String> getKeys() {
        return Arrays.asList("Key1", "Key2");
    }
}
