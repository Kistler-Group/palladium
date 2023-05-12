package com.AMS.injection.subtypefactory;

/**
 * Indicates that no provider was found for a given key.
 */
public class ProviderNotFoundException extends RuntimeException {

    public ProviderNotFoundException(Object key) {
        super("Unsupported type " + key + " requested!");
    }
}
