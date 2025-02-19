package com.danielmeinicke.lson;

import com.danielmeinicke.lson.path.filter.Parameter;
import org.jetbrains.annotations.NotNull;

public interface JsonBoolean extends JsonPrimitive, Parameter {

    // Static initializers

    static @NotNull JsonBoolean create(boolean b) {
        return new JsonBooleanImpl(b);
    }

}
