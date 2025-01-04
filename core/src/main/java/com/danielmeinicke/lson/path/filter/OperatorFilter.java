package com.danielmeinicke.lson.path.filter;

import com.danielmeinicke.lson.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class OperatorFilter implements Filter {

    private final @NotNull Parameter primary;
    private final @NotNull Parameter secondary;

    private final @NotNull Operator operator;

    public OperatorFilter(@NotNull Parameter primary, @NotNull Parameter secondary, @NotNull Operator operator) {
        this.primary = primary;
        this.secondary = secondary;
        this.operator = operator;
    }

    // Getters

    @Override
    public @NotNull Parameter getPrimary() {
        return primary;
    }
    public @NotNull Parameter getSecondary() {
        return secondary;
    }

    public @NotNull Operator getOperator() {
        return operator;
    }

    // Modules

    @Override
    public boolean validate(@NotNull Json json) {
        return false;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof OperatorFilter)) return false;
        @NotNull OperatorFilter that = (OperatorFilter) object;
        return Objects.equals(getPrimary(), that.getPrimary()) && Objects.equals(getSecondary(), that.getSecondary()) && getOperator() == that.getOperator();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getPrimary(), getSecondary(), getOperator());
    }

    @Override
    public @NotNull String toString() {
        return getPrimary() + " " + getOperator() + " " + getSecondary();
    }

    // Classes

    /**
     * Defines the types of logical operations that can be applied to filters
     * in JSONPath expressions. These operators enable complex querying by
     * allowing the combination of multiple conditions in a logical manner.
     * <p>
     * Each operator has an associated symbol and precedence level, which
     * determines the order in which operations are evaluated. Higher precedence
     * operators are evaluated before lower precedence ones. For example,
     * the {@link #AND} operator has a higher precedence than the {@link #OR}
     * operator.
     * </p>
     */
    public enum Operator {

        /**
         * Represents a logical AND operation, where all conditions must be true.
         * <p>
         * Example: {@code ?(@.price > 10 && @.stock > 0)} will return
         * results only if both conditions are met.
         * </p>
         */
        AND("AND", 2),

        /**
         * Represents a logical OR operation, where at least one condition must be true.
         * <p>
         * Example: {@code ?(@.price < 10 || @.price > 50)} will return results
         * if either of the conditions is met.
         * </p>
         */
        OR("OR", 1),

        /**
         * Performs an equality comparison, verifying that two values are identical.
         * <p>
         * Example: {@code ?(@.name == "John")} will return results where the
         * name is exactly "John".
         * </p>
         */
        EQUAL("==", 3),

        /**
         * Performs an inequality comparison, ensuring two values are different.
         * <p>
         * Example: {@code ?(@.age != 30)} will return results where the age
         * is not equal to 30.
         * </p>
         */
        DIFFERENT("!=", 3),

        /**
         * Checks whether one value is numerically less than another.
         * <p>
         * Example: {@code ?(@.price < 100)} will return results where the
         * price is less than 100.
         * </p>
         */
        LESS_THAN("<", 4),

        /**
         * Checks if one value is less than or equal to another.
         * <p>
         * Example: {@code ?(@.stock <= 0)} will return results where the
         * stock is less than or equal to zero.
         * </p>
         */
        LESS_THAN_OR_EQUAL("<=", 4),

        /**
         * Verifies if one value is greater than another.
         * <p>
         * Example: {@code ?(@.rating > 4.5)} will return results where the
         * rating is greater than 4.5.
         * </p>
         */
        MORE_THAN(">", 4),

        /**
         * Verifies if one value is greater than or equal to another.
         * <p>
         * Example: {@code ?(@.quantity >= 1)} will return results where the
         * quantity is greater than or equal to 1.
         * </p>
         */
        MORE_THAN_OR_EQUAL(">=", 4);

        private final @NotNull String symbol;
        private final int precedency;

        /**
         * Constructs a new logical operator with the specified symbol representation
         * and precedence level.
         *
         * @param symbol the symbol representing this logical operator (e.g., "AND", "OR").
         * @param precedency the precedence level of the operator, determining the order of evaluation.
         */
        Operator(@NotNull String symbol, int precedency) {
            this.symbol = symbol;
            this.precedency = precedency;
        }

        // Getters

        /**
         * Retrieves the symbol representation of this logical operator.
         *
         * @return the string representation (e.g., "AND", "OR").
         */
        public @NotNull String getSymbol() {
            return symbol;
        }

        /**
         * Retrieves the precedence level of this logical operator.
         *
         * @return the precedence level as an integer.
         */
        public int getPrecedency() {
            return precedency;
        }

        // Implementations

        /**
         * Returns a string representation of this logical operator.
         *
         * @return the symbol representation of the logical operator.
         */
        @Override
        public @NotNull String toString() {
            return getSymbol();
        }
    }
}
