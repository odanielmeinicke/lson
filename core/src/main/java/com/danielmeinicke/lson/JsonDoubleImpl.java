package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;

final class JsonDoubleImpl implements JsonDouble {

    // Object

    private final double d;

    public JsonDoubleImpl(double d) {
        this.d = d;
    }

    // Numbers

    @Override
    public @NotNull String getAsString() {
        return String.valueOf(d);
    }
    @Override
    public boolean getAsBoolean() {
        if (d == 0 || d == 1) {
            return d == 1;
        }

        throw new UnsupportedOperationException("cannot parse this json double into a boolean");
    }
    @Override
    public int getAsInteger() {
        return (int) d;
    }
    @Override
    public byte getAsByte() {
        return (byte) d;
    }
    @Override
    public float getAsFloat() {
        return (float) d;
    }
    @Override
    public double getAsDouble() {
        return d;
    }
    @Override
    public short getAsShort() {
        return (short) d;
    }
    @Override
    public long getAsLong() {
        return (long) d;
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
        return false;
    }
    @Override
    public boolean isBoolean() {
        return false;
    }

    // Casts

    @Override
    public @NotNull JsonObject getAsObject() {
        throw new UnsupportedOperationException("cannot parse a json double into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        throw new UnsupportedOperationException("cannot parse a json double into a json array");
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
        return new JsonDoubleImpl(d);
    }

    // Comparable

    @Override
    public int compareTo(@NotNull Number o) {
        if (o instanceof Double) {
            return Double.compare(getAsDouble(), o.doubleValue());
        } else {
            throw new IllegalArgumentException("only Double instances are supported");
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonDouble)) return false;
        @NotNull JsonDouble that = (JsonDouble) object;
        return getAsDouble() == that.getAsDouble();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getAsDouble());
    }

    @Override
    public @NotNull String toString() {
        return String.valueOf(d);
    }

}
