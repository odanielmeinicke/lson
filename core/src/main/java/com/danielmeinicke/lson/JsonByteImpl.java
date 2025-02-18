package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;

final class JsonByteImpl implements JsonByte {

    // Object

    private final byte b;

    public JsonByteImpl(byte b) {
        this.b = b;
    }

    // Numbers

    @Override
    public @NotNull String getAsString() {
        return toString();
    }
    @Override
    public boolean getAsBoolean() {
        if (b == 0 || b == 1) {
            return b == 1;
        }

        throw new UnsupportedOperationException("cannot parse this json byte into a boolean");
    }
    @Override
    public int getAsInteger() {
        return b;
    }
    @Override
    public byte getAsByte() {
        return b;
    }
    @Override
    public float getAsFloat() {
        return b;
    }
    @Override
    public double getAsDouble() {
        return b;
    }
    @Override
    public short getAsShort() {
        return b;
    }
    @Override
    public long getAsLong() {
        return b;
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
        return b == 0 || b == 1;
    }

    // Casts

    @Override
    public @NotNull JsonObject getAsObject() {
        throw new UnsupportedOperationException("cannot parse a json byte into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        throw new UnsupportedOperationException("cannot parse a json byte into a json array");
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
        writer.write(b);
    }
    @Override
    public void write(@NotNull OutputStream stream) throws IOException {
        stream.write(b);
    }

    // Cloneable

    @Override
    public @NotNull Json clone() {
        return new JsonByteImpl(b);
    }

    // Comparable

    @Override
    public int compareTo(@NotNull Number o) {
        if (o instanceof Byte) {
            return Byte.compare(getAsByte(), o.byteValue());
        } else {
            throw new IllegalArgumentException("only Byte instances are supported");
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonByte)) return false;
        @NotNull JsonByte jsonByte = (JsonByte) object;
        return getAsByte() == jsonByte.getAsByte();
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getAsByte());
    }

    @Override
    public @NotNull String toString() {
        return String.valueOf(b);
    }

}
