package com.danielmeinicke.lson.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a selector used in JSON Path expressions to navigate and query JSON structures.
 * A selector identifies specific elements, ranges, or patterns within a JSON document.
 * <p>
 * This interface includes various nested subinterfaces and implementations for
 * specific types of selectors, such as {@link Name}, {@link Slicing}, {@link Wildcard},
 * {@link Repeatable}, and {@link Index}. These types represent key components
 * of JSON Path syntax.
 * </p>
 */
public interface Selector extends Serializable, Cloneable {

    /**
     * Provides a string representation of the selector. This representation should
     * be suitable for debugging and logging purposes, and must accurately reflect
     * the properties of the implementing selector.
     *
     * @return a non-null string representation of this selector.
     */
    @Override
    @NotNull String toString();

    /**
     * Represents a JSON Path name selector, which matches a property name or key in a JSON object.
     * A {@code Name} selector is used to access specific properties within a JSON structure.
     * <p>
     * This interface also provides utility methods for parsing strings into multiple {@code Name}
     * instances, allowing for JSON Path expressions with quoted or escaped names.
     * </p>
     */
    // todo: replace with JsonString
    interface Name extends Repeatable, CharSequence {

        /**
         * Parses a given string into an array of {@link Name} objects. The string may include
         * quoted names, with support for escaped quotes.
         * <p>
         * Example:
         * <pre>
         *     String input = "'key1', \"key2\", 'key\\'3'";
         *     Name[] names = Name.read(input);
         * </pre>
         * <p>
         * Regex explanation for matching names:
         * <ul>
         * <li>{@code (?<!\\\\)} - Ensures that a quote is not preceded by an escape character.</li>
         * <li>{@code (['\"])} - Matches either a single or double quote.</li>
         * <li>{@code (.*?)} - Lazily captures the content of the quote.</li>
         * <li>{@code \\1} - Ensures the closing quote matches the opening quote.</li>
         * </ul>
         *
         * @param string the input string containing quoted JSON Path names.
         * @return an array of parsed {@code Name} objects.
         * @throws IllegalArgumentException if the input string has invalid formatting.
         */
        static @NotNull Name @NotNull [] read(@NotNull String string) {
            @NotNull List<Name> list = new LinkedList<>();
            @NotNull Pattern pattern = Pattern.compile("(?<!\\\\)(['\"])(.*?)(?<!\\\\)\\1");
            @NotNull Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                @NotNull String read = matcher.group(2);
                list.add(new Builder.NameImpl(read));
            }
            return list.toArray(new Name[0]);
        }
    }

    /**
     * Represents an array slicing operation in JSON Path. Array slicing allows
     * access to a subset of array elements, defined by a start index, an optional
     * end index, and a step value.
     * <p>
     * The syntax follows the format {@code [start:end:step]}, where:
     * <ul>
     * <li>{@code start} - The inclusive starting index of the slice.</li>
     * <li>{@code end} - The exclusive ending index of the slice (optional).</li>
     * <li>{@code step} - The interval between elements (default is 1).</li>
     * </ul>
     * Examples:
     * <ul>
     * <li>{@code [0:3]} selects the first three elements (indices 0, 1, and 2).</li>
     * <li>{@code [::2]} selects every second element in the array.</li>
     * </ul>
     */
    interface Slicing extends Selector {

        /**
         * Retrieves the inclusive starting index of the array slice.
         *
         * @return the start index.
         */
        int getStart();

        /**
         * Retrieves the exclusive ending index of the array slice. If not specified,
         * the slice extends to the end of the array.
         *
         * @return the end index, or {@code null} if not specified.
         */
        @Nullable Integer getEnd();

        /**
         * Retrieves the step value for the slice. The step determines the interval
         * between selected elements in the array.
         *
         * @return the step value.
         */
        int getStep();
    }

    /**
     * Represents a wildcard operator in JSON Path. The wildcard matches all elements
     * in a JSON array or all properties in a JSON object.
     * <p>
     * Example usages:
     * <ul>
     * <li>{@code $.*} - Matches all properties in the root object.</li>
     * <li>{@code $[*]} - Matches all properties in the root object.</li>
     * </ul>
     */
    interface Wildcard extends Selector {
    }

    /**
     * Represents a selector that can repeat or iterate over multiple matching elements
     * in a JSON structure. This is a marker interface for selectors like {@link Index}
     * that are inherently iterable or repeatable.
     */
    interface Repeatable extends Selector {
    }

    /**
     * Represents a selector for a specific position within a JSON array. This is used
     * to access an individual element by its index.
     * <p>
     * For example, {@code $[2]} retrieves the element at index 2 of an array.
     */
    // todo: replace with JsonInteger
    interface Index extends Repeatable, Comparable<Integer> {
    }
}
