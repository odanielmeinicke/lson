package com.danielmeinicke.lson.factory;

import org.jetbrains.annotations.NotNull;

public abstract class JsonFactory {

    // Static initializers

    private static @NotNull JsonFactory factory;

    public static @NotNull JsonFactory getFactory() {
        return factory;
    }
    public static void setFactory(@NotNull JsonFactory factory) {
        JsonFactory.factory = factory;
    }

    // Object

}
