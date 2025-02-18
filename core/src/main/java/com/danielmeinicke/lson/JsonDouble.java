package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;

public interface JsonDouble extends JsonNumber {

    // Static initializers

    static @NotNull JsonDouble create(double d) {
        return new JsonDoubleImpl(d);
    }

}
