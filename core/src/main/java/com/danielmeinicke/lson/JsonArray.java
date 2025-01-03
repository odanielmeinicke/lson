package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

// todo: not add the same instance at #add
public interface JsonArray extends Json, List<@Nullable Json> {
    default boolean addAll(@NotNull JsonArray array) {
        return addAll((Collection<? extends Json>) array);
    }
    default boolean addAll(int index, @NotNull JsonArray array) {
        return addAll(index, (Collection<? extends Json>) array);
    }
}
