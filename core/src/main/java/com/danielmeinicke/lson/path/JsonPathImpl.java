package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.JsonInteger;
import com.danielmeinicke.lson.JsonString;
import com.danielmeinicke.lson.exception.path.NodeParseException;
import com.danielmeinicke.lson.path.filter.*;
import com.danielmeinicke.lson.path.filter.ArithmeticOperatorFilter.ArithmeticOperator;
import com.danielmeinicke.lson.path.filter.ComparisonOperatorFilter.ComparisonOperator;
import com.danielmeinicke.lson.path.segment.Segment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class JsonPathImpl implements JsonPath {

    // Static initializers

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
                throw new IllegalStateException("bracket at index" + (brackets.size() == 1 ? "" : "es") + " " + brackets.toString().replace("[", "").replace("]", "") + " not closed");
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
    static @NotNull Selector readSelector(@NotNull String selector, boolean inside) {
        selector = selector.trim();

        // Parse
        @NotNull Selector instance;

        if (selector.equals("*")) { // Wildcard
            // Create selector
            instance = new Builder.WildcardImpl();
        } else if (Integer.getInteger(selector) != null || selector.matches("^\\d+(?:\\s*,\\s*\\d+)*$")) { // Indexes
            // Retrieve parts
            @NotNull String[] parts = selector.split("\\s*,\\s*");
            @NotNull List<JsonInteger> indexes = new LinkedList<>();

            for (@NotNull String part : parts) {
                indexes.add(JsonInteger.create(Integer.parseInt(part)));
            }

            // Create selector
            if (indexes.size() > 1) {
                instance = new Builder.RepeatableImpl(indexes);
            } else {
                instance = indexes.get(0);
            }
        } else if (selector.startsWith("'") || selector.startsWith("\"")) { // Names
            // Retrieve parts
            @NotNull String[] parts = selector.split("\\s*,\\s*");
            @NotNull List<JsonString> names = new LinkedList<>();

            for (@NotNull String part : parts) {
                names.add(JsonString.create(part));
            }

            // Create selector
            if (names.size() > 1) {
                instance = new Builder.RepeatableImpl(names);
            } else {
                instance = names.get(0);
            }
        } else if (selector.startsWith("@") || selector.startsWith("$")) {
            instance = readNode(selector);
        } else if ((inside || selector.startsWith("?")) && (selector.length() > 3 && selector.charAt(inside ? 0 : 1) == '(' && selector.charAt(selector.length() - 1) == ')')) { // Filter
            selector = selector.substring(inside ? 1 : 2, selector.length() - 1);
            @Nullable String parameter = null;

            @NotNull Inside parenthesis = new Inside();
            boolean quotes = false;
            boolean inverted = selector.charAt(0) == '!';

            // Parameters
            @Nullable Parameter primary = null;
            @Nullable Operator operator = null;
            @Nullable Parameter secondary = null;

            // Node
            @NotNull String node = selector.startsWith("!") ? selector.substring(1) : selector;
            node = node.trim();

            if (node.startsWith("@") || node.startsWith("$")) {
                primary = readNode(node);
            } else {
                char[] chars = selector.toCharArray();
                for (int row = 0; row < chars.length; row++) {
                    char c = chars[row];
                    @Nullable Character previous = row > 0 ? chars[row - 1] : null;
                    @Nullable Character next = row + 1 > chars.length ? chars[row + 1] : null;

                    // Variables
                    if ((c == '"' || c == '\'') && (previous == null || previous != '\\')) {
                        quotes = !quotes;
                    } else if (!quotes && c == '(') {
                        parenthesis.add(row);
                    } else if (!quotes && c == ')') {
                        int start = parenthesis.remove();
                        parameter = selector.substring(start, row + 1);
                    } else {
                        if (!quotes && operator == null && (c == '+' || c == '-' || c == '*' || c == '/' || c == '%')) {
                            operator = ArithmeticOperator.getBySymbol(String.valueOf(c));
                        } else if (!quotes && operator == null && (next != null && ((c == '&' && next == '&') || (c == '|' && next == '|') || (c == '=' && next == '=') || (c == '!' && next == '=') || (c == '<' && next == '=') || (c == '>' && next == '='))) || (c == '>' || c == '<')) {
                            operator = ComparisonOperator.getBySymbol(String.valueOf(c));

                            if (operator == null && next != null) {
                                operator = ComparisonOperator.getBySymbol(new String(new char[]{c, next}));
                            } else {
                                throw new NodeParseException("cannot parse comparison operator: " + (next != null ? new String(new char[]{c, next}) : String.valueOf(c)));
                            }
                        } else {
                            continue;
                        }

                        // Primary
                        @NotNull String p = selector.substring(0, row);
                        primary = (Parameter) readSelector(p, true);

                        // Secondary
                        @NotNull String s = selector.substring(row + 1);
                        s = s.trim();

                        if (s.startsWith("(") && !s.endsWith(")")) {
                            s = s + ")";
                        }

                        secondary = (Parameter) readSelector(s, true);
                    }

                    // Parse parameter
                    if (parameter != null) try {
                        if (primary == null) {
                            primary = (Parameter) readSelector(parameter, true);
                        } else if (secondary == null) {
                            secondary = (Parameter) readSelector(parameter, true);
                        }
                    } catch (@NotNull NodeParseException e) {
                        throw new NodeParseException("cannot parse selector's parameter: '" + parameter + "'", e);
                    } catch (@NotNull ClassCastException e) {
                        throw new NodeParseException("invalid selector parameter: '" + parameter + "'", e);
                    }
                }
            }

            if (operator instanceof ArithmeticOperator) {
                instance = new Builder.FilterImpl(new ArithmeticOperatorFilter(primary, secondary, (ArithmeticOperator) operator));
            } else if (operator instanceof ComparisonOperator) {
                instance = new Builder.FilterImpl(new ComparisonOperatorFilter(primary, secondary, (ComparisonOperator) operator));
            } else {
                if (!(primary instanceof Node)) {
                    throw new NodeParseException("the parameter '" + primary + "' must be a node to parse the existence filter: " + selector);
                }

                instance = new ExistenceFilter((Node) primary, inverted);
            }
        } else if (selector.contains(":")) { // Array Slicing
            // Split parts
            @NotNull String[] parts = selector.split(":", -1); // -1 para preservar partes vazias
            if (parts.length < 2 || parts.length > 3) {
                throw new NodeParseException("array slicing selector's format invalid: " + selector);
            }

            // Variables
            int start = 0;
            @Nullable Integer end = null;
            int step = 1;

            // Parse start
            {
                @NotNull String string = parts[0].trim();
                if (!string.isEmpty()) try {
                    start = Integer.parseInt(string);
                } catch (@NotNull NumberFormatException e) {
                    throw new NodeParseException("invalid array slicing's start value: " + string);
                }
            }

            // Parse end
            {
                @NotNull String string = parts[1].trim();
                if (!string.isEmpty()) try {
                    end = Integer.parseInt(string);
                } catch (@NotNull NumberFormatException e) {
                    throw new NodeParseException("invalid array slicing's end value: " + string);
                }
            }

            // Parse step
            if (parts.length == 3) {
                @NotNull String string = parts[2].trim();
                if (!string.isEmpty()) try {
                    step = Integer.parseInt(string);
                } catch (@NotNull NumberFormatException e) {
                    throw new NodeParseException("invalid array slicing's step value: " + string);
                }
            }

            // Finish
            instance = new Builder.SlicingImpl(start, end, step);
        } else {
            throw new UnsupportedOperationException("unknown selector type: '" + selector + "'");
        }

        // Finish
        return instance;
    }

    public static void main(String[] args) {
        // Variables
        @NotNull String path = "?(5+(1+(2+(3+(4+(5+(6+(7+(8+(9+1))))))))))";

        {
            // ‘Warm-up’ to avoid JVM optimizations at the first tests
            for (int i = 0; i < 100; i++) {
                readSelector(path, false);
            }

            // Generate timing results
            long totalNanoTime = 0;
            int times = 1000;

            for (int i = 0; i < times; i++) {
                long start = System.nanoTime();
                readSelector(path, false);
                long duration = System.nanoTime() - start;
                totalNanoTime += duration;
            }

            long media = totalNanoTime / times;
            System.out.println("Took ±" + TimeUnit.NANOSECONDS.toMillis(media) + "ms (" + media + "ns) to parse path.");
        }

        // Generate tree
        @NotNull Selector selector = readSelector(path, false);

        System.out.println("Json Path tree:");

        if (selector instanceof Parameter) {
            @NotNull Parameter parameter = (Parameter) selector;
            printTree(parameter, "  ");
        } else {
            System.out.println("  |- " + selector + " (" + selector.getClass().getSimpleName().replace("Impl", "") + ")");
        }
    }
    private static void printTree(@NotNull Parameter parameter, @NotNull String prefix) {
        if (parameter instanceof Filter && !(parameter instanceof ArithmeticOperatorFilter) && !(parameter instanceof ComparisonOperatorFilter) && !(parameter instanceof ExistenceFilter)) {
            @NotNull Filter filter = (Filter) parameter;
            printTree(((Filter) parameter).getPrimary(), prefix);
        } else {
            System.out.println(prefix + "|- " + parameter + " (" + parameter.getClass().getSimpleName().replace("Impl", "") + ")");

            if (parameter instanceof ArithmeticOperatorFilter) {
                @NotNull ArithmeticOperatorFilter arithmetic = (ArithmeticOperatorFilter) parameter;
                printTree(arithmetic.getPrimary(), prefix + "  ");
                printTree(arithmetic.getSecondary(), prefix + "  ");
            } else if (parameter instanceof ComparisonOperatorFilter) {
                @NotNull ComparisonOperatorFilter comparison = (ComparisonOperatorFilter) parameter;
                printTree(comparison.getPrimary(), prefix + "  ");
                printTree(comparison.getSecondary(), prefix + "  ");
            } else if (parameter instanceof ExistenceFilter) {
                @NotNull ExistenceFilter comparison = (ExistenceFilter) parameter;
                printTree(comparison.getPrimary(), prefix + "  ");
            }
        }
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

    // Classes

    private static final class Inside implements Iterable<Integer> {

        private final @NotNull LinkedList<Integer> list = new LinkedList<>();

        public void add(int index) {
            if (list.contains(index)) {
                return;
            }

            list.add(index);
        }
        public int remove() {
            return list.removeLast();
        }

        public int size() {
            return list.size();
        }

        // Implementations

        @Override
        public @NotNull Iterator<Integer> iterator() {
            return list.iterator();
        }

    }

}
