package com.danielmeinicke.lson.path.filter;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.path.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ExistenceFilter implements Filter {

    private final @NotNull Node node;
    private final boolean inverted;

    public ExistenceFilter(@NotNull Node node) {
        this.node = node;
        this.inverted = false;
    }
    public ExistenceFilter(@NotNull Node node, boolean inverted) {
        this.node = node;
        this.inverted = inverted;
    }

    // Getters

    @Override
    public @NotNull Node getPrimary() {
        return node;
    }
    public boolean isInverted() {
        return inverted;
    }

    // Modules

    @Override
    public boolean validate(@NotNull Json json) {
        return false;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof ExistenceFilter)) return false;
        @NotNull ExistenceFilter that = (ExistenceFilter) object;
        return Objects.equals(getPrimary(), that.getPrimary());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getPrimary());
    }

    @Override
    public @NotNull String toString() {
        return (isInverted() ? "!" : "") + getPrimary();
    }

}
