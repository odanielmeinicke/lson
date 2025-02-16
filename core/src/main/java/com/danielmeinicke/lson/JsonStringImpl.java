package com.danielmeinicke.lson;

import com.danielmeinicke.lson.exception.JsonNumberException;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

final class JsonStringImpl implements JsonString {

    // Object

    private final @NotNull String string;
    private final int[] escapes;

    public JsonStringImpl(@NotNull String string) {
        this.string = string;

        // Look for escapes
        @NotNull List<Integer> escapes = new ArrayList<>();
        int index = -1;

        int row = 0;
        while ((index = string.indexOf("\"", index + 1)) != -1) {
            escapes.add(index + row);
            row++;
        }

        // Convert list to array
        this.escapes = escapes.stream().mapToInt(Integer::intValue).toArray();
    }
    @Internal
    private JsonStringImpl(@NotNull String string, int[] escapes) {
        this.string = string;
        this.escapes = escapes;
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
        if (string.isEmpty()) return false;

        boolean hasDecimal = false;
        boolean hasExponent = false;
        int start = (string.charAt(0) == '-' || string.charAt(0) == '+') ? 1 : 0;

        for (int i = start; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c >= '0' && c <= '9') continue;
            if (c == '.' && !hasDecimal && !hasExponent) {
                hasDecimal = true;
            } else if ((c == 'e' || c == 'E') && !hasExponent && i > start) {
                hasExponent = true;
                if (i + 1 < string.length() && (string.charAt(i + 1) == '-' || string.charAt(i + 1) == '+')) {
                    i++;
                }
            } else {
                return false;
            }
        }

        return start < string.length();
    }
    @Override
    public boolean isString() {
        return true;
    }
    @Override
    public boolean isBoolean() {
        return !string.isEmpty() && string.length() <= 5 && (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false"));
    }

    // Casts

    @Override
    public @NotNull JsonObject getAsObject() {
        throw new UnsupportedOperationException("cannot parse a json string into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        throw new UnsupportedOperationException("cannot parse a json string into a json array");
    }
    @Override
    public @NotNull JsonPrimitive getAsPrimitive() {
        return this;
    }
    @SuppressWarnings("unchecked")
    @Override
    public @NotNull JsonNumber getAsNumber() {
        // Functions that try to convert the string into a number
        @NotNull Function<String, JsonNumber>[] converters = new Function[] {
                str -> new JsonByteImpl(Byte.parseByte((String) str)),
                str -> new JsonShortImpl(Short.parseShort((String) str)),
                str -> new JsonIntegerImpl(Integer.parseInt((String) str)),
                str -> new JsonLongImpl(Long.parseLong((String) str)),
                str -> new JsonDoubleImpl(Double.parseDouble((String) str)),
                str -> new JsonFloatImpl(Float.parseFloat((String) str))
        };

        // Try to parse
        @NotNull String string = getAsString();
        for (@NotNull Function<String, JsonNumber> converter : converters) {
            try {
                return converter.apply(string);
            } catch (NumberFormatException ignore) {
            }
        }

        // Fail
        throw new JsonNumberException("cannot parse '" + string + "' into a valid number");
    }

    // Primitives

    @Override
    public @NotNull String getAsString() {
        return string;
    }
    @Override
    public boolean getAsBoolean() {
        if (string.length() <= 5) {
            if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(string);
            }
        }

        throw new UnsupportedOperationException("cannot parse this json string into a boolean");
    }
    @Override
    public int getAsInteger() {
        return Integer.parseInt(getAsString());
    }
    @Override
    public double getAsDouble() {
        return Double.parseDouble(getAsString());
    }
    @Override
    public float getAsFloat() {
        return Float.parseFloat(getAsString());
    }
    @Override
    public short getAsShort() {
        return Short.parseShort(getAsString());
    }
    @Override
    public long getAsLong() {
        return Long.parseLong(getAsString());
    }
    @Override
    public byte getAsByte() {
        return Byte.parseByte(getAsString());
    }

    // Getters

    @Override
    public long footprint() {
        return string.length() + escapes.length + 2;
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
        return new JsonStringImpl(string, escapes);
    }

    // CharSequence

    @Override
    public int length() {
        return string.length();
    }
    @Override
    public char charAt(int index) {
        return string.charAt(index);
    }
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return string.subSequence(start, end);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof JsonString)) return false;
        @NotNull JsonString that = (JsonString) object;
        return Objects.equals(getAsString(), that.getAsString());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getAsString());
    }

    @Override
    public @NotNull String toString() {
        if (escapes.length == 0) {
            return "\"" + string + "\"";
        } else {
            @NotNull StringBuilder builder = new StringBuilder(string);
            for (int index : escapes) {
                builder.insert(index, "\\");
            }

            // Finish
            return builder.toString();
        }
    }

}
