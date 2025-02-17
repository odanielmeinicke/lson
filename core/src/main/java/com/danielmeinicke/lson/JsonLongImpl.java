package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;

final class JsonLongImpl implements JsonLong {

    // Object

    private final long l;

    public JsonLongImpl(long l) {
        this.l = l;
    }

    // Numbers

    @Override
    public @NotNull String getAsString() {
        return String.valueOf(l);
    }
    @Override
    public boolean getAsBoolean() {
        if (l == 0 || l == 1) {
            return l == 1;
        }

        throw new UnsupportedOperationException("cannot parse this json long into a boolean");
    }
    @Override
    public int getAsInteger() {
        return (int) l;
    }
    @Override
    public byte getAsByte() {
        return (byte) l;
    }
    @Override
    public float getAsFloat() {
        return l;
    }
    @Override
    public double getAsDouble() {
        return l;
    }
    @Override
    public short getAsShort() {
        return (short) l;
    }
    @Override
    public long getAsLong() {
        return l;
    }

    // Verifications

    @Override
    public boolean isObject() {
        return false;
    }
    @Override
    public boolean isArray() {
        return false;
    }
    @Override
    public boolean isPrimitive() {
        return true;
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
        return l == 0 || l == 1;
    }

    // Casts

    @Override
    public @NotNull JsonObject getAsObject() {
        throw new UnsupportedOperationException("cannot parse a json long into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        throw new UnsupportedOperationException("cannot parse a json long into a json array");
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
        return new JsonLongImpl(l);
    }

    // Comparable

    @Override
    public int compareTo(@NotNull Number o) {
        if (o instanceof Long) {
            return Long.compare(getAsShort(), o.shortValue());
        } else {
            throw new IllegalArgumentException("only Long instances are supported");
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonLong)) return false;
        @NotNull JsonLong that = (JsonLong) object;
        return Objects.equals(getAsLong(), that.getAsLong());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getAsLong());
    }

    @Override
    public @NotNull String toString() {
        return getAsString();
    }

}
