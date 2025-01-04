package com.danielmeinicke.lson.path.filter;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a parameter in the context of JSONPath.
 * <p>
 * A value can encompass various data types that yield a single result, including:
 *
 * <h3>JSON Primitives</h3>
 * <ul>
 *     <li>Numerical literals (e.g., {@code 42}, {@code 3.14})</li>
 *     <li>String literals (e.g., {@code "Hello, World!"})</li>
 *     <li>Boolean values (e.g., {@code true}, {@code false})</li>
 * </ul>
 *
 * <h3>JSON Nodes</h3>
 * <ul>
 *     <li>Nodes extracted from a JSON structure (e.g., {@code @.name}, {@code @.age})</li>
 * </ul>
 *
 * <h3>Expressions</h3>
 * <ul>
 *     <li>Arithmetic or logical expressions (e.g., {@code (@.number + 5)}, {@code (3 > 2)}) that can be used in filters (e.g., {@code ?(5 + (@.number + 5))})</li>
 * </ul>
 *
 * </p>
 *
 * <h2>Method Summary:</h2>
 * <ul>
 *     <li>
 *         {@link #toString()} - Returns a string representation of the value.
 *     </li>
 * </ul>
 *
 * @see Comparable
 */
public interface Parameter {

    @Override
    @NotNull String toString();

}
