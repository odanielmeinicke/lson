package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;

final class JsonShortImpl implements JsonShort {

    // Object

    private final short s;

    public JsonShortImpl(short s) {
        this.s = s;
    }

    // Numbers

    @Override
    public @NotNull String getAsString() {
        return String.valueOf(s);
    }
    @Override
    public boolean getAsBoolean() {
        if (s == 0 || s == 1) {
            return s == 1;
        }

        throw new UnsupportedOperationException("cannot parse this json short into a boolean");
    }
    @Override
    public int getAsInteger() {
        return s;
    }
    @Override
    public byte getAsByte() {
        return (byte) s;
    }
    @Override
    public float getAsFloat() {
        return s;
    }
    @Override
    public double getAsDouble() {
        return s;
    }
    @Override
    public short getAsShort() {
        return s;
    }
    @Override
    public long getAsLong() {
        return s;
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
        return s == 0 || s == 1;
    }

    // Casts

    @Override
    public @NotNull JsonObject getAsObject() {
        throw new UnsupportedOperationException("cannot parse a json short into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        throw new UnsupportedOperationException("cannot parse a json short into a json array");
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
        return new JsonShortImpl(s);
    }

    // Comparable

    @Override
    public int compareTo(@NotNull Number o) {
        if (o instanceof Short) {
            return Short.compare(getAsShort(), o.shortValue());
        } else {
            throw new IllegalArgumentException("only Short instances are supported");
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonShort)) return false;
        @NotNull JsonShort that = (JsonShort) object;
        return Objects.equals(getAsShort(), that.getAsShort());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getAsShort());
    }

    @Override
    public @NotNull String toString() {
        return getAsString();
    }

}
