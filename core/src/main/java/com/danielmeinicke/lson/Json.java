package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

/**
 * Essa interface representa um Json.
 *
 * 1. Caso seja um Json String, mas esse string esteja no formato de um numero tipo "123", ao executar #getAsNumber ou #getAsInteger uma instância válida do numero respectivo será criado. Essa regra não se aplica ao JsonObject nem JsonArray.
 */
public interface Json extends Serializable, Cloneable {

    // Static initializers

    // Object

    boolean isObject();
    boolean isPrimitive();
    boolean isArray();

    boolean isNumber();
    boolean isString();
    boolean isBoolean();

    @NotNull JsonObject getAsObject();
    @NotNull JsonArray getAsArray();
    @NotNull JsonPrimitive getAsPrimitive();
    @NotNull JsonNumber getAsNumber();

    @NotNull String getAsString();
    boolean getAsBoolean();
    int getAsInteger();
    double getAsDouble();
    float getAsFloat();
    short getAsShort();
    long getAsLong();
    byte getAsByte();

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

    void write(@NotNull Writer writer) throws IOException;
    void write(@NotNull OutputStream stream) throws IOException;

    // Cloneable

    @NotNull Json clone();

    // Implementations

    @Override
    @NotNull String toString();

}
