package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.path.segment.Segment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class JsonPathImpl implements JsonPath {

    // Static initializers

    /**
     * Creates a {@link JsonPath} instance by parsing the given string representation of a JSON path.
     *
     * @param string the string representation of the JSON path, which must not be null or invalid.
     * @return a {@code JsonPath} instance that corresponds to the given string.
     * @throws IllegalArgumentException if the string is null, empty, or contains an invalid JSON path format.
     */
    static @NotNull JsonPath readPath(@NotNull String string) {
        final @NotNull String original = string;
        @NotNull List<Node> nodes = new LinkedList<>();

        // Split all nodes at the string
        {
            @NotNull List<Integer> split = new LinkedList<>();

            boolean escaped = false;
            boolean quotes = false; // true if a ' or " has found
            @NotNull LinkedList<Integer> brackets = new LinkedList<>(); // a list containing all '[' indexes

            int row = 0;
            for (char character : original.toCharArray()) {
                final boolean isQuotes = (character == '"' || character == '\'');

                // Escaped
                if (character == '\\') {
                    escaped = true;
                }

                // Mark quotes
                if (!escaped && isQuotes) {
                    quotes = !quotes;
                }

                // Check if is a node split
                if (character == '.' && brackets.isEmpty()) {
                    split.add(row);
                } else if (!quotes) {
                    if (character == '[') {
                        brackets.add(row);
                    } else if (character == ']') {
                        if (!brackets.isEmpty()) {
                            brackets.removeLast();
                        } else {
                            throw new IllegalStateException("illegal close bracket at index " + row + ": " + original);
                        }
                    }
                }

                // Finish
                if (character != '\\') {
                    escaped = false;
                }

                row++;
            }

            if (!brackets.isEmpty()) {
                throw new IllegalStateException("bracked at index" + (brackets.size() == 1 ? "" : "es") + " " + brackets.toString().replace("[", "").replace("]", "") + " not closed");
            }

            // Finish
            for (row = 0; row < split.size(); row++) {
                // Get start and end substring indexes
                int start = split.get(row);
                int end = row + 1 < split.size() ? split.get(row + 1) : original.length();

                // Remove split nodes
                if (original.charAt(start) == '.') start += 1;
                else start += 2;

                // Add node to list
                nodes.add(readNode(original.substring(start, end).trim()));
            }
        }

        // Finish
        return new JsonPathImpl(nodes.toArray(new Node[0]), original);
    }
    static @NotNull Node readNode(@NotNull String node) {
        return new Builder.NodeImpl("test", node, new Segment[0], null);
    }
    static @NotNull Segment readSegment(@NotNull String segment) {
        return null;
    }
    private static @NotNull Selector readSelector(@NotNull String selector) {
        return null;
    }

    // Object

    private final @NotNull Node @NotNull [] nodes;
    private final @NotNull String original;

    public JsonPathImpl(@NotNull Node @NotNull [] nodes, @NotNull String original) {
        this.nodes = nodes;
        this.original = original;
    }

    // Getters

    @Override
    public @NotNull Node @NotNull [] getNodes() {
        return nodes;
    }

    // Modules

    @Override
    public boolean contains(@NotNull Json json) {
        return false;
    }

    @Override
    public @Nullable Json get(@NotNull Json json) {
        return null;
    }

    // CharSequence

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof JsonPath)) return false;
        @NotNull JsonPath path = (JsonPath) object;
        return Objects.deepEquals(getNodes(), path.getNodes());
    }
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(getNodes()), original);
    }

    @Override
    public @NotNull String toString() {
        return original;
    }

}
