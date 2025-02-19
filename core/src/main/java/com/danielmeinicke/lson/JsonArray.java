package com.danielmeinicke.lson;

import com.danielmeinicke.lson.path.Queryable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

// todo: not add the same instance at #add
public interface JsonArray extends Json, Queryable, List<@Nullable Json> {

    // Static initializers

    static @NotNull JsonArray create(@NotNull List<Json> elements) {
        return new JsonArrayImpl(elements);
    }
    static @NotNull JsonArray create() {
        return new JsonArrayImpl(new LinkedList<>());
    }

    // Object

    default boolean addAll(@NotNull JsonArray array) {
        return addAll((Collection<? extends Json>) array);
    }
    default boolean addAll(int index, @NotNull JsonArray array) {
        return addAll(index, (Collection<? extends Json>) array);
    }

    @NotNull JsonArray deepClone();

}
