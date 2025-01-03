package com.danielmeinicke.lson.exception.path;

import org.jetbrains.annotations.NotNull;

public final class NodeParseException extends RuntimeException {
    public NodeParseException(@NotNull String message) {
        super(message);
    }
    public NodeParseException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
