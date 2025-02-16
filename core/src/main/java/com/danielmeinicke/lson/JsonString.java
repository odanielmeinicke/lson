package com.danielmeinicke.lson;

import com.danielmeinicke.lson.path.Selector;
import com.danielmeinicke.lson.path.filter.Parameter;
import org.jetbrains.annotations.NotNull;

public interface JsonString extends Selector, Parameter, JsonPrimitive, CharSequence {

    // Static initializers

    static @NotNull JsonString create(char @NotNull [] characters) {
        return new JsonStringImpl(new String(characters));
    }
    static @NotNull JsonString create(@NotNull StringBuilder builder) {
        return new JsonStringImpl(builder.toString());
    }
    static @NotNull JsonString create(@NotNull String string) {
        return new JsonStringImpl(string);
    }

}
