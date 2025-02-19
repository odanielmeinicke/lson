package com.danielmeinicke.lson;

import com.danielmeinicke.lson.path.JsonPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

final class JsonArrayImpl implements JsonArray {

    // Object

    private final @NotNull List<Json> elements;

    public JsonArrayImpl(@NotNull List<Json> elements) {
        this.elements = elements;
    }

    // Verifications

    @Override
    public boolean isObject() {
        return false;
    }
    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }
    @Override
    public boolean isNumber() {
        return false;
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
        throw new UnsupportedOperationException("cannot parse a json array into a json object");
    }
    @Override
    public @NotNull JsonArray getAsArray() {
        return this;
    }
    @Override
    public @NotNull JsonPrimitive getAsPrimitive() {
        throw new UnsupportedOperationException("cannot parse a json array into a json primitive");
    }
    @Override
    public @NotNull JsonNumber getAsNumber() {
        throw new UnsupportedOperationException("cannot parse a json array into a json number");
    }
    @Override
    public @NotNull String getAsString() {
        throw new UnsupportedOperationException("cannot parse a json array into a string");
    }
    @Override
    public boolean getAsBoolean() {
        throw new UnsupportedOperationException("cannot parse a json array into a boolean");
    }
    @Override
    public int getAsInteger() {
        throw new UnsupportedOperationException("cannot parse a json array into an integer");
    }
    @Override
    public double getAsDouble() {
        throw new UnsupportedOperationException("cannot parse a json array into a double");
    }
    @Override
    public float getAsFloat() {
        throw new UnsupportedOperationException("cannot parse a json array into a float");
    }
    @Override
    public short getAsShort() {
        throw new UnsupportedOperationException("cannot parse a json array into a short");
    }
    @Override
    public long getAsLong() {
        throw new UnsupportedOperationException("cannot parse a json array into a long");
    }
    @Override
    public byte getAsByte() {
        throw new UnsupportedOperationException("cannot parse a json array into a byte");
    }

    // Getters

    @Override
    public long footprint() {
        return stream().mapToLong(json -> json == null ? 4 : json.footprint()).sum();
    }

    // Writers

    @Override
    public void write(@NotNull Writer writer) throws IOException {
        writer.write("[");
        for (@Nullable Json json : this) {
            writer.write(json != null ? json.toString() : "null");
        }
        writer.write("]");
    }
    @Override
    public void write(@NotNull OutputStream stream) throws IOException {
        stream.write('[');
        for (@Nullable Json json : this) {
            stream.write((json != null ? json.toString() : "null").getBytes());
        }
        stream.write(']');
    }

    // Cloneable

    @Override
    public @NotNull Json clone() {
        return new JsonArrayImpl(elements);
    }
    @Override
    public @NotNull JsonArray deepClone() {
        // Generate elements list
        @NotNull List<Json> elements = this.elements.stream().map((json) -> {
            if (json instanceof JsonArray) {
                return ((JsonArray) json).deepClone();
            } else if (json instanceof JsonObject) {
                return ((JsonObject) json).deepClone();
            } else {
                return json.clone();
            }
        }).collect(Collectors.toList());

        // Generate new instance
        return new JsonArrayImpl(elements);
    }

    // Query

    @Override
    public @Nullable Json query(@NotNull JsonPath path) {
        return null;
    }
    @Override
    public void set(@NotNull JsonPath path, @Nullable Json json) {

    }
    @Override
    public void remove(@NotNull JsonPath path) {

    }

    // Collection

    @Override
    public int size() {
        return elements.size();
    }
    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return elements.contains(o);
    }

    @Override
    public @NotNull Iterator<@Nullable Json> iterator() {
        return elements.iterator();
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return elements.toArray();
    }
    @Override
    public @NotNull <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean add(@Nullable Json json) {
        return elements.add(json);
    }
    @Override
    public boolean remove(@Nullable Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(elements).containsAll(c);
    }
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends @Nullable Json> c) {
        return elements.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends @Nullable Json> c) {
        return elements.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public @Nullable Json get(int index) {
        return elements.get(index);
    }

    @Override
    public @Nullable Json set(int index, @Nullable Json element) {
        return elements.set(index, element);
    }

    @Override
    public void add(int index, @Nullable Json element) {
        elements.add(index, element);
    }

    @Override
    public @Nullable Json remove(int index) {
        return elements.remove(index);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return elements.indexOf(o);
    }
    @Override
    public int lastIndexOf(@Nullable Object o) {
        return elements.lastIndexOf(o);
    }

    @Override
    public @NotNull ListIterator<@Nullable Json> listIterator() {
        return elements.listIterator();
    }
    @Override
    public @NotNull ListIterator<@Nullable Json> listIterator(int index) {
        return elements.listIterator(index);
    }

    @Override
    public @NotNull List<@Nullable Json> subList(int fromIndex, int toIndex) {
        return elements.subList(fromIndex, toIndex);
    }

    // Implementations

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JsonArray)) {
            return false;
        } else {
            @NotNull JsonArray that = (JsonArray) object;
            return size() == that.size() && footprint() == that.footprint() && new HashSet<>(this).containsAll(that);
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder("[");

        for (@Nullable Json json : this) {
            builder.append(json);
        }

        builder.append("]");
        return builder.toString();
    }

}
