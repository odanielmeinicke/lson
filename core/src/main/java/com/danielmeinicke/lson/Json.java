package com.danielmeinicke.lson;

import com.danielmeinicke.lson.path.JsonPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

public interface Json extends Serializable, Cloneable {

    // Static initializers

    // Object

    boolean isObject();
    boolean isPrimitive();
    boolean isArray();

    boolean isNumber();
    boolean isString();

    @NotNull JsonObject getAsObject();
    @NotNull JsonArray getAsArray();
    @NotNull JsonPrimitive getAsPrimitive();

    @NotNull String getAsString();

    // Query

    @NotNull Json query(@NotNull JsonPath path);
    void remove(@NotNull JsonPath path);

    void set(@NotNull JsonPath path, @Nullable Json json);

    // Getters

    /**
     * Retorna o tamanho total em bytes desse json, não diretamente alocado na memória.
     * O json pode estar alocado em qualquer outro lugar, esse método retorna com precisão
     * a quantidade de bytes que ele tem no total
     *
     * @return
     */
    long footprint();

    // Writers

    void write(@NotNull Writer writer);
    void write(@NotNull OutputStream stream);

    // Cloneable

    @NotNull Json clone();

    // Implementations

    @Override
    @NotNull String toString();

}
