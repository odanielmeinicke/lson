package com.danielmeinicke.lson.path.filter;

import com.danielmeinicke.lson.path.filter.ArithmeticOperatorFilter.ArithmeticOperator;
import com.danielmeinicke.lson.path.filter.ComparisonOperatorFilter.ComparisonOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Operator {

    // Static initializers

    static @Nullable Operator getBySymbol(@NotNull String string) {
        @Nullable Operator operator = ArithmeticOperator.getBySymbol(string);

        if (operator != null) {
            return operator;
        }

        return ComparisonOperator.getBySymbol(string);
    }

    // Object

    @NotNull String getSymbol();
}
