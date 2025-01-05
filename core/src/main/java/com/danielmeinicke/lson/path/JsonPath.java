package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.JsonPrimitive;
import com.danielmeinicke.lson.exception.path.NodeNotFoundException;
import com.danielmeinicke.lson.path.Node.Type;
import com.danielmeinicke.lson.path.Selector.Name;
import com.danielmeinicke.lson.path.Selector.Repeatable;
import com.danielmeinicke.lson.path.filter.ExistenceFilter;
import com.danielmeinicke.lson.path.filter.Filter;
import com.danielmeinicke.lson.path.filter.OperatorFilter;
import com.danielmeinicke.lson.path.filter.OperatorFilter.Operator;
import com.danielmeinicke.lson.path.segment.Segment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a JSON path that provides access to elements within a JSON structure.
 * This interface extends both {@link Serializable} and {@link CharSequence}, enabling
 * serialization and character-based operations.
 *
 * <p>A {@code JsonPath} is a sequence of nodes that can be used to navigate and extract
 * data from a JSON document. Each node represents a step in the path, which may include
 * keys, array indices, or other JSON constructs.</p>
 */
public interface JsonPath extends Serializable, CharSequence {

    // Static initializers

    public static void main(String[] args) {
        @NotNull JsonPath path = read("$.node[?(@.name < 10)][?(@.name)][?(@.name['test'])].e.['test . []', 'ada'][0][ 3 3 ][0:][::12]");

        for (@NotNull Node node : path.getNodes()) {
            System.out.println(node + ":");
            for (@NotNull Segment segment : node.getSegments()) {
                System.out.println("  " + segment + ": " + segment.getClass().getSimpleName() + " - " + segment.stream().toArray(Selector[]::new)[0].getClass().getSimpleName());
            }
        }

//        System.out.println(path);
    }

    /**
     * Creates a {@link JsonPath} instance by parsing the given string representation of a JSON path.
     *
     * @param string the string representation of the JSON path, which must not be null or invalid.
     * @return a {@code JsonPath} instance that corresponds to the given string.
     * @throws IllegalArgumentException if the string is null, empty, or contains an invalid JSON path format.
     */
    static @NotNull JsonPath read(@NotNull String string) {
        final @NotNull String original = string;

        // Split nodes
        @NotNull List<String> stringNodes = new LinkedList<>();

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
                stringNodes.add(original.substring(start, end).trim());
            }
        }

        // Retrieve segments and generate node instance
        @NotNull List<Node> nodes = new LinkedList<>();

        for (@NotNull String node : stringNodes) {
            @Nullable Type type = null;

            // Determine node type
            for (@NotNull Type meta : Type.values()) {
                if (node.startsWith(String.valueOf(meta.getCharacter()))) {
                    type = meta;
                    break;
                }
            }

            // Retrieve names
            @NotNull LinkedList<StringBuilder> segmentsList = new LinkedList<>();
            @Nullable String name = null;

            {
                boolean brackets = node.startsWith("[");
                boolean naming = !brackets;

                boolean quotes = false;
                boolean escaped = false;

                // Parse nodes
                for (char character : node.toCharArray()) {
                    final boolean isQuotes = (character == '"' || character == '\'');

                    // Escaped
                    if (character == '\\') {
                        escaped = true;
                    }

                    // Mark quotes
                    if (!escaped && isQuotes) {
                        quotes = !quotes;
                    }

                    // Select name
                    if (character == '[' && !quotes) {
                        brackets = true;
                        naming = false;
                    } if (naming) {
                        name = (name == null ? "" : name) + character;
                    }

                    // Start segments
                    if (brackets) {
                        // Create a new segment
                        if (character == '[' && !quotes) {
                            segmentsList.add(new StringBuilder().append(character));
                        } else {
                            // Retrive last segment and add character to it
                            @NotNull StringBuilder segment = segmentsList.getLast();
                            segment.append(character);

                            if (character == ']' && !quotes) {
                                brackets = false;
                            }
                        }
                    }

                    // Finish
                    if (character != '\\') {
                        escaped = false;
                    }
                }
            }

            // Parse segments
            @NotNull List<Segment> segments = new LinkedList<>();

            for (@NotNull String segment : segmentsList.stream().map(StringBuilder::toString).collect(Collectors.toList())) {
                boolean quotesBefore = true;
                boolean quotes = false;
                boolean escaped = false;
                boolean comma = false;

                @Nullable Class<? extends Selector> scope = null;
                @NotNull List<Character> data = new LinkedList<>();

                // Segments
                @NotNull LinkedList<StringBuilder> names = new LinkedList<>();
                @NotNull StringBuilder index = new StringBuilder();

                @NotNull StringBuilder signal = new StringBuilder();
                @NotNull StringBuilder target = new StringBuilder();
                int s = 0;

                boolean inverted = false;
                @NotNull LinkedList<Integer> brackets = new LinkedList<>();

                // Start reading
                char[] chars = segment.toCharArray();
                for (int row = 0; row < chars.length ; row++) {
                    final char character = chars[row];
                    final boolean isQuotes = (character == '"' || character == '\'');

                    // Skip first bracket
                    if (row == 0) {
                        continue;
                    }

                    // Escaped
                    if (character == '\\') {
                        escaped = true;
                    }

                    // Mark quotes
                    if (!escaped && isQuotes) {
                        quotes = !quotes;
                        quotesBefore = !quotes;
                    } if (character == ' ' && !quotes && scope != OperatorFilter.class) {
                        continue;
                    }

                    // Determine selector scope
                    if (scope == null) {
                        if (character == '?') { // It's a filter selector
                            for (@NotNull Operator operator : Operator.values()) {
                                if (segment.contains(operator.getSymbol())) {
                                    scope = OperatorFilter.class;
                                }
                            }

                            if (scope == null) {
                                scope = ExistenceFilter.class;
                            }
                        } else if (character == '\'' || character == '"') { // It's a name selector
                            scope = Name.class;
                        } else if ((character >= '0' && character <= '9') || character == ':') { // It's an index or slicing selector
                            if (segment.contains(":")) {
                                scope = Selector.Slicing.class;
                            } else {
                                scope = Selector.Index.class;
                            }
                        } else if (character == '*') { // It's a wildcard selector
                            scope = Selector.Wildcard.class;
                        } else {
                            throw new IllegalStateException("invalid segment start character '" + character + "': " + segment);
                        }
                    }

                    if (scope == Name.class) {
                        if (character == ']' && !quotes) {
                            @NotNull List<Repeatable> repeatables = new LinkedList<>();

                            for (@NotNull StringBuilder builder : names) {
                                repeatables.add(new Builder.NameImpl(builder.toString()));
                            }

                            segments.add(new Builder.SegmentImpl(repeatables.toArray(new Repeatable[0])));
                        } else {
                            if (quotes && !names.isEmpty() && (!isQuotes || escaped)) {
                                @NotNull StringBuilder builder = names.getLast();
                                builder.append(character);
                            } if (character == ',') {
                                comma = true;
                            }

                            if (isQuotes && !quotesBefore) {
                                if ((comma || names.isEmpty())) {
                                    names.add(new StringBuilder());
                                    comma = false;
                                } else {
                                    throw new IllegalStateException("name selector missing comma between names: " + segment);
                                }
                            }
                        }
                    } else if (scope == Selector.Wildcard.class) {
                        segments.add(new Builder.SegmentImpl(new Builder.WildcardImpl()));
                    } else if (scope == Selector.Index.class) {
                        if (character == ']') {
                            int i;

                            try {
                                i = Integer.parseInt(index.toString());
                            } catch (@NotNull NumberFormatException e) {
                                throw new IllegalStateException("cannot parse index selector " + index, e);
                            }

                            segments.add(new Builder.SegmentImpl(new Builder.IndexImpl(i)));
                        } else {
                            index.append(character);
                        }
                    } else if (scope == Selector.Slicing.class) {
                        if (character == ']') {
                            @NotNull String @NotNull [] parts = index.toString().split(":");

                            int start = parts.length > 0 && !parts[0].isEmpty() ? Integer.parseInt(parts[0]) : 0;
                            @Nullable Integer end = parts.length > 1 && !parts[1].isEmpty() ? Integer.valueOf(parts[1]) : null;
                            int step = parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : 1;

                            segments.add(new Builder.SegmentImpl(new Builder.SlicingImpl(start, end, step)));
                        } else {
                            index.append(character);
                        }
                    } else if (scope == OperatorFilter.class) {
                        // ((a && b) || c)

                        if (character == '?') {
                            continue;
                        } else if (character == ' ') {
                            s++;
                        } else if (character == ']') {
                            System.out.println(target + " - " + signal + " - " + index);
                        } else if (s == 0) {
                            target.append(character);
                        } else if (s == 1) {
                            signal.append(character);
                        } else {
                            index.append(character);
                        }
                    } else if (scope == ExistenceFilter.class) {
                        // 5 == (d + 10)

                        if (character == '!' && !quotes) {
                            if (inverted) {
                                throw new IllegalStateException("this existence filter is already inverted");
                            } else if (index.length() > 0) {
                                throw new IllegalStateException("illegal character position '" + character + "': " + segment);
                            } else {
                                inverted = true;
                            }
                        }

                        if (character == '(' || character == ')' && brackets.isEmpty()) {
                            index.append(character);
                            continue;
                        } else if (character == '[') {
                            brackets.add(row);
                        } else if (character == ']' && !brackets.isEmpty()) {
                            brackets.removeLast();
                        }

                        index.append(character);
                        if (character == ']' && brackets.isEmpty()) {
                            segments.add(new Builder.SegmentImpl(new ExistenceFilter(new Builder.NodeImpl("test", index.toString(), new Segment[0], null))));
                        }
                    } else {
                        throw new UnsupportedOperationException("unsupported scope: " + scope.getName());
                    }

                    // Finish
                    if (character != '\\') {
                        escaped = false;
                    }

                    quotesBefore = false;
                }
            }

            nodes.add(new Builder.NodeImpl(name, node, segments.toArray(new Segment[0]), type));
        }

        // Finish creating the json path instance
        return new Builder.JsonPathImpl(nodes.toArray(new Node[0]), original);
    }

    // Object

    /**
     * Retrieves an array of {@link Node} objects representing the individual components
     * of this JSON path.
     *
     * @return a non-null array of {@code Node} objects. The array will always contain at least one node.
     */
    @NotNull Node @NotNull [] getNodes();

    /**
     * Determines if the given JSON object contains the element or structure specified by this JSON path.
     *
     * <p>This method evaluates the JSON path against the provided JSON object and checks
     * whether the path resolves to a valid element within the object.</p>
     *
     * @param json the {@link Json} object to be checked, which must not be null.
     * @return {@code true} if the JSON object contains the specified element or structure,
     *         {@code false} otherwise.
     * @throws IllegalArgumentException if the JSON object is null.
     */
    boolean contains(@NotNull Json json);

    /**
     * Retrieves the element or structure from the specified JSON object that corresponds to this JSON path.
     *
     * <p>If the JSON path resolves to a valid element, the method returns the element.
     * If the resolved element is a JSON null primitive, the method returns {@code null}.</p>
     *
     * @param json the {@link Json} object from which the element should be retrieved, which must not be null.
     * @return the resolved {@link Json} element, or {@code null} if the resolved element is a JSON null primitive.
     * @throws NodeNotFoundException if the JSON object does not contain the element or structure specified
     *                               by this JSON path.
     * @throws IllegalArgumentException if the JSON object is null.
     */
    @Nullable Json get(@NotNull Json json);

}
