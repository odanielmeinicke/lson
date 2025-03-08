package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.exception.path.NodeNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

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

    static @NotNull JsonPath parse(@NotNull String string) {
        return JsonPathImpl.readPath(string);
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
