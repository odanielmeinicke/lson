package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;

public interface JsonLong extends JsonNumber {

    // Static initializers

    static @NotNull JsonLong create(long l) {
        return new JsonLongImpl(l);
    }

}
