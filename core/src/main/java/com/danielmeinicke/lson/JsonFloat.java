package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;

public interface JsonFloat extends JsonNumber {

    // Static initializers

    static @NotNull JsonFloat create(float f) {
        return new JsonFloatImpl(f);
    }

}
