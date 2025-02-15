package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.path.filter.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.stream.Stream;

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
    interface Repeatable extends Selector, Iterable<Parameter> {
        @NotNull Stream<Parameter> stream();
    }

}
