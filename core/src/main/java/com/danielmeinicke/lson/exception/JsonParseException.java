package com.danielmeinicke.lson.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonParseException extends RuntimeException {

    private final int index;

    public JsonParseException(int index, @NotNull String message) {
        super(message);
        this.index = index;
    }
    public JsonParseException(int index, @NotNull String message, @Nullable Throwable cause) {
        super(message, cause);
        this.index = index;
    }

}
