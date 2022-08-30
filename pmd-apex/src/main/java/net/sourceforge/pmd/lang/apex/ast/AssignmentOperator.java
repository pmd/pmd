/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.AssignmentOp;

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
     * Returns a {@link AssignmentOperator} corresponding to the given {@link AssignmentOp}.
     */
    public static AssignmentOperator valueOf(AssignmentOp op) {
        switch (op) {
        case EQUALS:
            return EQUALS;
        case ADDITION_EQUALS:
            return ADDITION_EQUALS;
        case SUBTRACTION_EQUALS:
            return SUBTRACTION_EQUALS;
        case MULTIPLICATION_EQUALS:
            return MULTIPLICATION_EQUALS;
        case DIVISION_EQUALS:
            return DIVISION_EQUALS;
        case LEFT_SHIFT_EQUALS:
            return LEFT_SHIFT_EQUALS;
        case RIGHT_SHIFT_EQUALS:
            return RIGHT_SHIFT_SIGNED_EQUALS;
        case UNSIGNED_RIGHT_SHIFT_EQUALS:
            return RIGHT_SHIFT_UNSIGNED_EQUALS;
        case AND_EQUALS:
            return BITWISE_AND_EQUALS;
        case OR_EQUALS:
            return BITWISE_OR_EQUALS;
        case XOR_EQUALS:
            return BITWISE_XOR_EQUALS;
        default:
            throw new IllegalArgumentException("Invalid assignment operator " + op);
        }
    }
}
