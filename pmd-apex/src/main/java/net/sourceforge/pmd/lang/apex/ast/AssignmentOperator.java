/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.BinaryExpression;

/**
 * Apex assignment operator
 */
public enum AssignmentOperator {
    EQUALS("="),
    ADDITION_EQUALS("+="),
    SUBTRACTION_EQUALS("-="),
    MULTIPLICATION_EQUALS("*="),
    DIVISION_EQUALS("/="),
    LEFT_SHIFT_EQUALS("<<="),
    RIGHT_SHIFT_SIGNED_EQUALS(">>="),
    RIGHT_SHIFT_UNSIGNED_EQUALS(">>>="),
    BITWISE_AND_EQUALS("&="),
    BITWISE_OR_EQUALS("|="),
    BITWISE_XOR_EQUALS("^=");

    private final String symbol;

    AssignmentOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    /**
     * Returns a {@link AssignmentOperator} corresponding to the given {@link
     * BinaryExpression.Operator}. If {@code op} is {@code null}, {@link #EQUALS} is returned.
     */
    public static AssignmentOperator valueOf(BinaryExpression.Operator op) {
        if (op == null) {
            return EQUALS;
        }
        switch (op) {
        case ADDITION:
            return ADDITION_EQUALS;
        case SUBTRACTION:
            return SUBTRACTION_EQUALS;
        case MULTIPLICATION:
            return MULTIPLICATION_EQUALS;
        case DIVISION:
            return DIVISION_EQUALS;
        case LEFT_SHIFT:
            return LEFT_SHIFT_EQUALS;
        case RIGHT_SHIFT_SIGNED:
            return RIGHT_SHIFT_SIGNED_EQUALS;
        case RIGHT_SHIFT_UNSIGNED:
            return RIGHT_SHIFT_UNSIGNED_EQUALS;
        case BITWISE_AND:
            return BITWISE_AND_EQUALS;
        case BITWISE_OR:
            return BITWISE_OR_EQUALS;
        case BITWISE_XOR:
            return BITWISE_XOR_EQUALS;
        default:
            throw new IllegalArgumentException("Invalid assignment operator " + op);
        }
    }
}
