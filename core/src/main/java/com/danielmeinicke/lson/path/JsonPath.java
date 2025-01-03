package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Represents a JSON Path with enhanced functionalities, such as serialization,
 * string operations, and comparability. This interface serves as the primary
 * abstraction for handling complex JSON path operations, enabling easy manipulation
 * and querying of JSON data.
 */
public interface JsonPath extends Serializable, CharSequence {

    /**
     * Retrieves the nodes that constitute this JSON Path. Nodes can include
     * root nodes, child nodes, and other structural components of the path.
     *
     * @return a stream of {@link Node} objects representing the components of this JSON Path.
     */
    @NotNull Node @NotNull [] getNodes();

    /**
     * Represents a component of a JSON Path, such as a node or key. A node may point
     * to a specific object or property within the JSON structure and allows navigation
     * between hierarchical levels.
     */
    interface Node extends CharSequence {

        /**
         * Retrieves the name of this node. Could be '@' for a current node, '$' for a root node
         * or any other custom name.
         *
         * @return the name for this node
         */
        @NotNull String getName();

        /**
         * Retrieves the parent node of this node, representing the previous level
         * in the JSON Path hierarchy. If this node is the root, the parent is null.
         *
         * @return the parent {@link Node}, or null if this is the root node.
         */
        @Nullable
        Node getParent();

        /**
         * Retrieves the child node of this node, representing the next level
         * in the JSON Path hierarchy. If this node is a leaf node, the child is null.
         *
         * @return the child {@link Node}, or null if this is a leaf node.
         */
        @Nullable
        Node getChildren();

        /**
         * Checks if this node represents the root of a JSON Path. The root is typically
         * denoted by the "$" symbol.
         *
         * @return true if this node is the root; false otherwise.
         */
        default boolean isRoot() {
            return getName().trim().equals("$");
        }

        /**
         * Checks if this node represents the current node in a JSON Path. The current
         * node is typically denoted by the "@" symbol.
         *
         * @return true if this node represents the current node; false otherwise.
         */
        default boolean isCurrent() {
            return getName().trim().equals("@");
        }

        /**
         * Returns a stream of operators associated with this node.
         * A node could have multiples operators, since it doesn't conflict themselves.
         *
         * @return a stream in order containing all the operators
         */
        @NotNull Operator @NotNull [] getOperators();

    }

    /**
     * Represents an operator in JSON Path expressions. Operators are used in various
     * operations, such as filtering, slicing arrays, or performing logical comparisons.
     */
    interface Operator extends Serializable, Cloneable {
        /**
         * Converts the operator to its string representation. This is used for serialization
         * and generating JSON Path queries.
         *
         * @return the string representation of the operator.
         */
        @Override
        @NotNull String toString();
    }

    /**
     * Represents a comparator in JSON Path expressions. Comparators are used to compare
     * JSON elements, such as checking for equality or inequality.
     */
    interface Comparator extends Operator {
        /**
         * Converts the comparator to its string representation, which is typically the
         * symbol or keyword representing the operation (e.g., "==", "!=").
         *
         * @return the string representation of the comparator.
         */
        @Override
        @NotNull String toString();

        /**
         * Validates whether the comparator is applicable to a given JSON structure.
         * This method ensures the comparator can operate on the provided JSON data.
         *
         * @param json the JSON data to validate.
         * @return true if the comparator is valid for the provided JSON; false otherwise.
         */
        boolean validate(@NotNull Json json);
    }

    /**
     * Represents a logical comparator, which combines two other comparators with a logical
     * operation, such as 'AND' (&&) or 'OR' (||). Logical comparators allow for complex queries
     * involving multiple conditions.
     */
    interface LogicalComparator extends Comparator {

        /**
         * Retrieves the primary comparator in the logical operation.
         *
         * @return the primary {@link Comparator} used in this logical operation.
         */
        @NotNull Comparator getFirst();

        /**
         * Retrieves the secondary comparator in the logical operation.
         *
         * @return the secondary {@link Comparator} used in this logical operation.
         */
        @NotNull Comparator getSecond();

        /**
         * Retrieves the type of logical operation performed by this comparator.
         * The type defines whether the operation is an AND or an OR.
         *
         * @return the {@link Type} of logical operation.
         */
        @NotNull Type getType();

        /**
         * Automatically validates JSON path's value using both comparators
         *
         * @param json the JSON data to validate.
         * @return true if both comparators matches with the specified json
         */
        @Override
        default boolean validate(@NotNull Json json) {
            return getFirst().validate(json) && getSecond().validate(json);
        }

        /**
         * Represents the type of logical operation in a {@link LogicalComparator}.
         */
        enum Type {

            /** Logical AND operation, denoted by "&&". */
            AND("&&"),

            /** Logical OR operation, denoted by "||". */
            OR("||"),
            ;

            private final @NotNull String string;

            /**
             * Constructs a new logical operation type with the specified string representation.
             *
             * @param string the string representation of the logical operation.
             */
            Type(@NotNull String string) {
                this.string = string;
            }

            /**
             * Retrieves the string representation of this logical operation type.
             *
             * @return the string representation.
             */
            public @NotNull String getString() {
                return string;
            }

            /**
             * Converts the logical operation type to its string representation.
             *
             * @return the string representation of the logical operation type.
             */
            @Override
            public @NotNull String toString() {
                return getString();
            }
        }
    }

    /**
     * Represents a primitive comparator, which compares JSON primitive values.
     * These comparators can be used to evaluate conditions such as equality,
     * inequality, or numerical comparisons.
     */
    interface PrimitiveComparator extends Comparator {

        /**
         * Retrieves the node to the JSON element being compared.
         *
         * @return the {@link Node} representing the JSON element.
         */
        @NotNull Node getNode();

        /**
         * Retrieves the primitive value being compared against. This value
         * is the target of the comparison.
         *
         * @return the {@link JsonPrimitive} value to compare against, or null if none is set.
         */
        @Nullable JsonPrimitive getPrimitive();

        /**
         * Retrieves the type of comparison being performed. The type defines
         * the specific operation (e.g., equality, greater than, etc.).
         *
         * @return the {@link Type} of comparison.
         */
        @NotNull Type getType();

        /**
         * Automatically validates JSON path's value using the primitive comparator's type
         *
         * @param json the JSON data to validate.
         * @return true if the specified json matches with the comparator
         */
        @Override
        default boolean validate(@NotNull Json json) {
            if (!json.isPrimitive()) {
                throw new IllegalArgumentException("the primitive comparator's json validator must be a primitive!");
            }

            return getType().validate(json.getAsPrimitive());
        }

        /**
         * Defines the types of primitive comparisons available in JSON Path expressions.
         */
        enum Type {

            /** Equality comparison, denoted by "==". */
            EQUAL("==") {
                @Override
                public boolean validate(@NotNull JsonPrimitive primitive) {
                    return true;
                }
                @Override
                public boolean compare(@NotNull JsonPrimitive from, @NotNull JsonPrimitive with) {
                    return from.equals(with);
                }
            },

            /** Inequality comparison, denoted by "!=". */
            DIFFERENT("!=") {
                @Override
                public boolean validate(@NotNull JsonPrimitive primitive) {
                    return true;
                }
                @Override
                public boolean compare(@NotNull JsonPrimitive from, @NotNull JsonPrimitive with) {
                    return !from.equals(with);
                }
            },

            /** Less-than comparison, denoted by "<". */
            LESS_THAN("<") {
                @Override
                public boolean validate(@NotNull JsonPrimitive primitive) {
                    return primitive.isNumber();
                }
                @Override
                public boolean compare(@NotNull JsonPrimitive from, @NotNull JsonPrimitive with) {
                    return with.compareTo(from) < 0;
                }
            },

            /** Less-than-or-equal comparison, denoted by "<=". */
            LESS_THAN_OR_EQUAL("<=") {
                @Override
                public boolean validate(@NotNull JsonPrimitive primitive) {
                    return primitive.isNumber();
                }
                @Override
                public boolean compare(@NotNull JsonPrimitive from, @NotNull JsonPrimitive with) {
                    return with.compareTo(from) <= 0;
                }
            },

            /** Greater-than comparison, denoted by ">". */
            MORE_THAN(">") {
                @Override
                public boolean validate(@NotNull JsonPrimitive primitive) {
                    return primitive.isNumber();
                }
                @Override
                public boolean compare(@NotNull JsonPrimitive from, @NotNull JsonPrimitive with) {
                    return with.compareTo(from) > 0;
                }
            },

            /** Greater-than-or-equal comparison, denoted by ">=". */
            MORE_THAN_OR_EQUAL(">=") {
                @Override
                public boolean validate(@NotNull JsonPrimitive primitive) {
                    return primitive.isNumber();
                }
                @Override
                public boolean compare(@NotNull JsonPrimitive from, @NotNull JsonPrimitive with) {
                    return with.compareTo(from) >= 0;
                }
            },
            ;

            private final @NotNull String string;

            /**
             * Constructs a new comparison type with the specified string representation.
             *
             * @param string the string representation of the comparison type.
             */
            Type(@NotNull String string) {
                this.string = string;
            }

            /**
             * Retrieves the string representation of this comparison type.
             *
             * @return the string representation.
             */
            public @NotNull String getString() {
                return string;
            }

            /**
             * Validates whether the comparison type is applicable to the provided JSON primitive.
             *
             * @param primitive the JSON primitive to validate.
             * @return true if the comparison type is valid; false otherwise.
             */
            public abstract boolean validate(@NotNull JsonPrimitive primitive);

            /**
             * Performs the comparison between two JSON primitives based on this type.
             *
             * @param from the first JSON primitive.
             * @param with the second JSON primitive.
             * @return true if the comparison condition is met; false otherwise.
             */
            public abstract boolean compare(@NotNull JsonPrimitive from, @NotNull JsonPrimitive with);

            /**
             * Converts the comparison type to its string representation.
             *
             * @return the string representation of the comparison type.
             */
            @Override
            public @NotNull String toString() {
                return getString();
            }
        }
    }

    /**
     * Represents a wildcard operator in JSON Path expressions. Wildcards are used
     * to match multiple elements in a JSON structure, such as all properties or array items.
     */
    interface Wildcard extends Operator {
    }

    /**
     * Represents a specific position within an array in JSON Path expressions.
     * This operator allows access to an individual array element by its index.
     */
    interface ArrayPosition extends Operator, Comparable<Integer> {
    }

    /**
     * Represents an array slice in JSON Path expressions. Slices allow access
     * to a range of elements within an array, defined by a start index, an optional
     * end index, and a step value.
     */
    interface ArraySlice extends Operator {

        /**
         * Retrieves the starting index of the slice. The start index is inclusive.
         *
         * @return the start index of the array slice.
         */
        int getStart();

        /**
         * Retrieves the ending index of the slice, if specified. The end index is exclusive.
         * If the end index is null, the slice extends to the end of the array.
         *
         * @return the end index of the array slice, or null if not specified.
         */
        @Nullable Integer getEnd();

        /**
         * Retrieves the step value of the slice. The step defines the interval
         * between consecutive elements in the slice.
         *
         * @return the step value of the array slice.
         */
        int getStep();
    }

}
