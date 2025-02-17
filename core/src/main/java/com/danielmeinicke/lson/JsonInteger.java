package com.danielmeinicke.lson;

import com.danielmeinicke.lson.path.Selector;
import org.jetbrains.annotations.NotNull;

public interface JsonInteger extends JsonNumber, Selector {

    // Static initializers

    static @NotNull JsonInteger create(int i) {
        return new JsonIntegerImpl(i);
    }

}
