package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.exception.path.NodeNotFoundException;
import com.danielmeinicke.lson.path.filter.Parameter;
import com.danielmeinicke.lson.path.segment.Segment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a component of a JSON Path, such as a node or key.
 * A {@code Node} is an abstraction that identifies a specific part of a JSON structure.
 * Nodes allow navigation between hierarchical levels in JSON documents and represent
 * various JSON Path constructs, including the root (`$`), current (`@`), and deep-scan (`..`) nodes.
 *
 * <p>Nodes may or may not have names. For example:
 * <ul>
 *   <li>The root node (`$`) has no specific name but represents the top level of a JSON document.</li>
 *   <li>The current node (`@`) refers to the current position in the JSON hierarchy and is unnamed.</li>
 *   <li>Deep-scan (`..`) nodes indicate a recursive descent and are also unnamed.</li>
 * </ul>
 *
 * <p>This interface extends {@link CharSequence}, allowing operations on textual representations
 * of JSON Path components, such as string manipulation or parsing.</p>
 */
public interface Node extends CharSequence, Parameter, Selector {

    /**
     * Retrieves the name of this node, if applicable.
     * Not all nodes have names. For example:
     * <ul>
     *   <li>The root node (`$`) is unnamed but represents the top level of the JSON structure.</li>
     *   <li>The current node (`@`) is unnamed and refers to the current context in a JSON Path.</li>
     *   <li>The deep-scan node (`..`) is also unnamed and denotes a recursive search in the JSON hierarchy.</li>
     * </ul>
     *
     * @return the name of this node, or {@code null} if the node is unnamed.
     */
    @Nullable String getName();

    /**
     * Retrieves the {@link Type} of this node, which represents its role in the JSON Path.
     * Types include:
     * <ul>
     *   <li>{@link Type#ROOT} - Denotes the root (`$`) node of the JSON document.</li>
     *   <li>{@link Type#CURRENT} - Refers to the current (`@`) node in the hierarchy.</li>
     *   <li>{@link Type#DEEP_SCAN} - Represents the deep-scan (`..`) operator for recursive searches.</li>
     * </ul>
     *
     * @return the {@link Type} of this node.
     */
    @Nullable Type getType();

    /**
     * Retrieves an array of {@link Segment} objects representing the components
     * of this node's path. Segments are the individual steps that form the complete
     * JSON Path to the current node.
     *
     * @return a non-null array of {@code Segment} objects, each representing a step in the JSON Path.
     */
    @NotNull Segment @NotNull [] getSegments();

    /**
     * Checks if this node represents the root of a JSON Path.
     * The root node is always the starting point of a JSON Path and is denoted by the `$` symbol.
     *
     * <p>The root node allows access to the entire JSON document and serves as the context for
     * the rest of the path. It has no parent and is always at the top of the hierarchy.</p>
     *
     * @return {@code true} if this node represents the root; {@code false} otherwise.
     */
    default boolean isRoot() {
        return getType() == Type.ROOT;
    }

    /**
     * Checks if this node represents the current node in a JSON Path.
     * The current node is denoted by the `@` symbol and refers to the context
     * in which the JSON Path is evaluated.
     *
     * <p>The current node is used to apply filters or expressions relative to
     * the current position in the JSON hierarchy.</p>
     *
     * @return {@code true} if this node represents the current node; {@code false} otherwise.
     */
    default boolean isCurrent() {
        return getType() == Type.CURRENT;
    }

    /**
     * Checks if this node represents a deep-scan node.
     * The deep-scan operator (`..`) is used in JSON Path to recursively search for elements
     * at any level of the JSON document hierarchy.
     *
     * <p>For example, the path `$..name` will return all elements named `name`
     * within the JSON document, regardless of their depth.</p>
     *
     * @return {@code true} if this node represents a deep-scan operator; {@code false} otherwise.
     */
    default boolean isDeepScan() {
        return getType() == Type.DEEP_SCAN;
    }

    /**
     * Checks if the specified JSON object contains the element or structure
     * identified by this node.
     *
     * @param json the {@link Json} object to check, which must not be null.
     * @return {@code true} if the JSON object contains the element specified by this node;
     *         {@code false} otherwise.
     * @throws IllegalArgumentException if the {@code json} parameter is null.
     */
    boolean contains(@NotNull Json json);

    /**
     * Retrieves the element or structure from the specified JSON object
     * that corresponds to this node's location.
     *
     * <p>If the JSON Path resolves to a valid element, the method returns it.
     * If the resolved element is a JSON null primitive, the method returns {@code null}.</p>
     *
     * @param json the {@link Json} object to retrieve the element from, which must not be null.
     * @return the resolved {@link Json} element, or {@code null} if the resolved element is a JSON null primitive.
     * @throws NodeNotFoundException if the JSON object does not contain the element specified by this node.
     * @throws IllegalArgumentException if the {@code json} parameter is null.
     */
    @Nullable Json get(@NotNull Json json);

    /**
     * Enumeration representing the types of nodes that can exist in a JSON Path.
     * Each type corresponds to a specific JSON Path construct or operator.
     */
    enum Type {

        /** Represents the root (`$`) node of a JSON document. */
        ROOT('$'),

        /** Represents the current (`@`) node in the JSON hierarchy. */
        CURRENT('@'),

        /** Represents the deep-scan (`..`) operator for recursive searches. */
        DEEP_SCAN('.');

        private final char character;

        Type(char character) {
            this.character = character;
        }

        /**
         * Retrieves the character representation of this node type.
         *
         * @return the character associated with this node type.
         */
        public char getCharacter() {
            return character;
        }

        @Override
        public @NotNull String toString() {
            return String.valueOf(getCharacter());
        }
    }
}