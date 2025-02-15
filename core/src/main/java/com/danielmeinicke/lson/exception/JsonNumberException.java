package com.danielmeinicke.lson.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonNumberException extends NumberFormatException {
    public JsonNumberException(@NotNull String message) {
        super(message);
    }
}
