package com.danielmeinicke.lson;

import com.danielmeinicke.lson.path.JsonPath;
import com.danielmeinicke.lson.path.Queryable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;

public interface JsonObject extends Json, Queryable, Map<@NotNull String, @Nullable Json> {

    void sort(@NotNull Comparator<Json> comparator);

    @NotNull Json extract(@NotNull JsonPath path);

    @NotNull JsonObject flatten();
    @NotNull JsonObject unflatten();

    void merge(@NotNull JsonObject object);
    void merge(@NotNull JsonObject object, boolean override);

    @NotNull JsonObject deepClone();

}
