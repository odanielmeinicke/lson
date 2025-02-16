package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;

public interface JsonShort extends JsonInteger {

    // Static initializers

    static @NotNull JsonShort create(short s) {
        return new JsonShortImpl(s);
    }

}
