package com.danielmeinicke.lson.path.filter;

import com.danielmeinicke.lson.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ArithmeticOperatorFilter implements Filter {

    private final @NotNull Parameter primary;
    private final @NotNull Parameter secondary;

    private final @NotNull ArithmeticOperator operator;

    public ArithmeticOperatorFilter(@NotNull Parameter primary, @NotNull Parameter secondary, @NotNull ArithmeticOperator operator) {
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

    public @NotNull ArithmeticOperator getOperator() {
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
        if (!(object instanceof ArithmeticOperatorFilter)) return false;
        @NotNull ArithmeticOperatorFilter that = (ArithmeticOperatorFilter) object;
        return Objects.equals(getPrimary(), that.getPrimary()) && Objects.equals(getSecondary(), that.getSecondary()) && getOperator() == that.getOperator();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getPrimary(), getSecondary(), getOperator());
    }

    @Override
    public @NotNull String toString() {
        return (getPrimary() instanceof Filter ? "(" + getPrimary() + ")" : getPrimary()) + " " + getOperator() + " " + (getSecondary() instanceof Filter ? "(" + getSecondary() + ")" : getSecondary());
    }

    // Classes

    public enum ArithmeticOperator implements Operator {

        PLUS("+"),
        MINUS("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        MODULO("%"),
        ;

        // Static initializers

        public static @Nullable ArithmeticOperator getBySymbol(@NotNull String code) {
            for (@NotNull ArithmeticOperator operator : values()) {
                if (operator.getSymbol().equalsIgnoreCase(code)) {
                    return operator;
                }
            }

            return null;
        }

        // Object

        private final @NotNull String symbol;

        ArithmeticOperator(@NotNull String symbol) {
            this.symbol = symbol;
        }

        // Getters

        @Override
        public @NotNull String getSymbol() {
            return symbol;
        }

        // Implementations
        
        @Override
        public @NotNull String toString() {
            return getSymbol();
        }

    }

}
