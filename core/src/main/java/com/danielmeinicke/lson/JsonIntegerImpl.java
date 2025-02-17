package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;

final class JsonIntegerImpl implements JsonInteger {

    // Object

    private final int i;

    public JsonIntegerImpl(int i) {
        this.i = i;
    }

    // Numbers

    @Override
    public @NotNull String getAsString() {
        return String.valueOf(i);
    }
    @Override
    public boolean getAsBoolean() {
        if (i == 0 || i == 1) {
            return i == 1;
        }

        throw new UnsupportedOperationException("cannot parse this json integer into a boolean");
    }
    @Override
    public int getAsInteger() {
        return i;
    }
    @Override
    public byte getAsByte() {
        return (byte) i;
    }
    @Override
    public float getAsFloat() {
        return i;
    }
    @Override
    public double getAsDouble() {
        return i;
    }
    @Override
    public short getAsShort() {
        return (short) i;
    }
    @Override
    public long getAsLong() {
        return i;
    }

    // Verifications

    @Override
    public boolean isObject() {
        return false;
    }
    @Override
    public boolean isPrimitive() {
        return true;
    }
    @Override
    public boolean isArray() {
        return false;
    }
    @Override
    public boolean isNumber() {
        return true;
    }
    @Override
    public boolean isString() {
        return true;
    }
    @Override
    public boolean isBoolean() {
        return i == 0 || i == 1;
    }

    // Casts

    @Override
    public @NotNull JsonObject getAsObject() {
        throw new UnsupportedOperationException("cannot parse a json integer into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        throw new UnsupportedOperationException("cannot parse a json integer into a json array");
    }
    @Override
    public @NotNull JsonPrimitive getAsPrimitive() {
        return this;
    }
    @Override
    public @NotNull JsonNumber getAsNumber() {
        return this;
    }

    // Getters

    @Override
    public long footprint() {
        return toString().length();
    }

    // Writers

    @Override
    public void write(@NotNull Writer writer) throws IOException {
        writer.write(toString());
    }
    @Override
    public void write(@NotNull OutputStream stream) throws IOException {
        stream.write(toString().getBytes());
    }

    // Cloneable

    @Override
    public @NotNull Json clone() {
        return new JsonIntegerImpl(i);
    }

    // Comparable

    @Override
    public int compareTo(@NotNull Number o) {
        if (o instanceof Integer) {
            return Integer.compare(getAsInteger(), o.intValue());
        } else {
            throw new IllegalArgumentException("only Integer instances are supported");
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonInteger)) return false;
        @NotNull JsonInteger that = (JsonInteger) object;
        return Objects.equals(getAsInteger(), that.getAsInteger());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getAsInteger());
    }

    @Override
    public @NotNull String toString() {
        return getAsString();
    }

}
