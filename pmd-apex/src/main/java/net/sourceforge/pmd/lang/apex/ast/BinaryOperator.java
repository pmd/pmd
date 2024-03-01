/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.BinaryExpression;

/**
 * Apex binary operator
 */
public enum BinaryOperator {
    ADDITION("+"),
    SUBTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/"),
    LEFT_SHIFT("<<"),
    RIGHT_SHIFT_SIGNED(">>"),
    RIGHT_SHIFT_UNSIGNED(">>>"),
    BITWISE_AND("&"),
    BITWISE_OR("|"),
    BITWISE_XOR("^"),
    NULL_COALESCING("??");

    private final String symbol;

    BinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    /**
     * Returns a {@link BinaryOperator} corresponding to the given {@link
     * BinaryExpression.Operator}.
     */
    public static BinaryOperator valueOf(BinaryExpression.Operator op) {
        switch (op) {
        case ADDITION:
            return ADDITION;
        case SUBTRACTION:
            return SUBTRACTION;
        case MULTIPLICATION:
            return MULTIPLICATION;
        case DIVISION:
            return DIVISION;
        case LEFT_SHIFT:
            return LEFT_SHIFT;
        case RIGHT_SHIFT_SIGNED:
            return RIGHT_SHIFT_SIGNED;
        case RIGHT_SHIFT_UNSIGNED:
            return RIGHT_SHIFT_UNSIGNED;
        case BITWISE_AND:
            return BITWISE_AND;
        case BITWISE_OR:
            return BITWISE_OR;
        case BITWISE_XOR:
            return BITWISE_XOR;
        case NULL_COALESCING:
            return NULL_COALESCING;
        default:
            throw new IllegalArgumentException("Invalid binary operator " + op);
        }
    }
}
