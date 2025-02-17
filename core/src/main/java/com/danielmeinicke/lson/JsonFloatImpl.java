package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;

final class JsonFloatImpl implements JsonFloat {

    // Object

    private final float f;

    public JsonFloatImpl(float f) {
        this.f = f;
    }

    // Numbers

    @Override
    public @NotNull String getAsString() {
        return String.valueOf(f);
    }
    @Override
    public boolean getAsBoolean() {
        if (f == 0 || f == 1) {
            return f == 1;
        }

        throw new UnsupportedOperationException("cannot parse this json float into a boolean");
    }
    @Override
    public int getAsInteger() {
        return (int) f;
    }
    @Override
    public byte getAsByte() {
        return (byte) f;
    }
    @Override
    public float getAsFloat() {
        return (float) f;
    }
    @Override
    public double getAsDouble() {
        return f;
    }
    @Override
    public short getAsShort() {
        return (short) f;
    }
    @Override
    public long getAsLong() {
        return (long) f;
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
        throw new UnsupportedOperationException("cannot parse a json float into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        throw new UnsupportedOperationException("cannot parse a json float into a json array");
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
        return new JsonFloatImpl(f);
    }

    // Comparable

    @Override
    public int compareTo(@NotNull Number o) {
        if (o instanceof Float) {
            return Float.compare(getAsFloat(), o.floatValue());
        } else {
            throw new IllegalArgumentException("only Float instances are supported");
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonFloat)) return false;
        @NotNull JsonFloat that = (JsonFloat) object;
        return getAsFloat() == that.getAsFloat();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getAsFloat());
    }

    @Override
    public @NotNull String toString() {
        return String.valueOf(f);
    }

}
