package com.AMS.injection.subtypesfactory.multi;

import com.AMS.injection.subtypefactory.SubTypeConstructable;

import java.util.Collection;

@SubTypeConstructable
interface Target {
    Collection<String> getKeys();
}