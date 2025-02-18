package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;

public interface JsonByte extends JsonNumber {

    // Static initializers

    static @NotNull JsonByte create(byte b) {
        return new JsonByteImpl(b);
    }

}
