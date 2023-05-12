package com.kistler.injection.subtypesfactory.multi;

import com.kistler.injection.subtypefactory.SubTypeConstructable;

import java.util.Collection;

@SubTypeConstructable
interface Target {
    Collection<String> getKeys();
}