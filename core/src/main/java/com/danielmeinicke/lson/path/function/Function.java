package com.danielmeinicke.lson.path.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Function {

    // Object

    private final @NotNull String name;

    public Function(@NotNull String name) {
        this.name = name;
    }

    // Getters

    public @NotNull String getName() {
        return name;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Function)) return false;
        @NotNull Function function = (Function) object;
        return Objects.equals(getName(), function.getName());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

}
