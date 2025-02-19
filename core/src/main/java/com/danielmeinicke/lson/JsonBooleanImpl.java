package com.danielmeinicke.lson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;

final class JsonBooleanImpl implements JsonBoolean {

    // Object

    // 0 = false, 1 = true
    private final byte b;

    public JsonBooleanImpl(boolean b) {
        this.b = (byte) (b ? 1 : 0);
    }
    private JsonBooleanImpl(byte b) {
        this.b = b;
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
        return true;
    }

    // Numbers

    @Override
    public @NotNull String getAsString() {
        return String.valueOf(b);
    }
    @Override
    public boolean getAsBoolean() {
        return (b != 0);
    }
    @Override
    public int getAsInteger() {
        return b;
    }
    @Override
    public double getAsDouble() {
        return b;
    }
    @Override
    public float getAsFloat() {
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
    @Override
    public byte getAsByte() {
        return b;
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
        return new JsonByteImpl(b);
    }

    // Getters

    @Override
    public long footprint() {
        return b == 0 ? 5 : 4;
    }

    // Writers

    @Override
    public void write(@NotNull Writer writer) throws IOException {
        writer.write(b == 0 ? "false" : "true");
    }
    @Override
    public void write(@NotNull OutputStream stream) throws IOException {
        stream.write(toString().getBytes());
    }

    // Cloneable

    @Override
    public @NotNull Json clone() {
        return new JsonBooleanImpl(b);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonBoolean)) return false;
        @NotNull JsonBoolean that = (JsonBoolean) object;
        return getAsBoolean() == that.getAsBoolean();
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getAsBoolean());
    }

    @Override
    public @NotNull String toString() {
        return b == 0 ? "false" : "true";
    }

}
