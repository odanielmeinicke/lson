package com.danielmeinicke.lson.exception.path;

import org.jetbrains.annotations.NotNull;

public final class NodeNotFoundException extends IllegalStateException {
    public NodeNotFoundException(@NotNull String message) {
        super(message);
    }
    public NodeNotFoundException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
