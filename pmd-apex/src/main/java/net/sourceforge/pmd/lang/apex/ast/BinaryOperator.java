/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.BinaryOp;

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
    BITWISE_XOR("^");

    private final String symbol;

    BinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    /**
     * Returns a {@link BinaryOperator} corresponding to the given {@link BinaryOp}.
     */
    public static BinaryOperator valueOf(BinaryOp op) {
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
        case RIGHT_SHIFT:
            return RIGHT_SHIFT_SIGNED;
        case UNSIGNED_RIGHT_SHIFT:
            return RIGHT_SHIFT_UNSIGNED;
        case AND:
            return BITWISE_AND;
        case OR:
            return BITWISE_OR;
        case XOR:
            return BITWISE_XOR;
        default:
            throw new IllegalArgumentException("Invalid binary operator " + op);
        }
    }
}
