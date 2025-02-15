package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Queryable {
    
    @Nullable Json query(@NotNull JsonPath path);
    void set(@NotNull JsonPath path, @Nullable Json json);
    void remove(@NotNull JsonPath path);

}
