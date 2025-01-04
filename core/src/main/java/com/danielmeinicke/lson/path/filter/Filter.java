package com.danielmeinicke.lson.path.filter;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.path.Selector;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a filter for JSON data, which evaluates JSON objects or primitives
 * against specific criteria or logical conditions. Filters are used to navigate
 * and query JSON structures in a flexible and dynamic way.
 */
public interface Filter extends Selector, Parameter {

    @NotNull
    Parameter getPrimary();

    /**
     * Validates whether the provided JSON data satisfies the criteria defined by
     * this filter. This method performs a structured evaluation based on the
     * specific implementation of the filter and its associated logic.
     *
     * @param json the JSON data to validate. Must not be null.
     * @return {@code true} if the JSON data matches the filter's criteria; {@code false} otherwise.
     * @throws IllegalArgumentException if the JSON data is not compatible with the filter's logic.
     */
    boolean validate(@NotNull Json json);

    // Classes

}