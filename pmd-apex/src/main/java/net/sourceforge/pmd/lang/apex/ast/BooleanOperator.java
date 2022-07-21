/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.BinaryExpression;

/**
 * Apex boolean operator
 */
public enum BooleanOperator {
    EQUAL("=="),
    NOT_EQUAL("!="),
    ALT_NOT_EQUAL("<>"),
    EXACTLY_EQUAL("==="),
    EXACTLY_NOT_EQUAL("!=="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN_OR_EQUAL(">="),
    LOGICAL_AND("&&"),
    LOGICAL_OR("||");

    private final String symbol;

    BooleanOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    /**
     * Returns a {@link BooleanOperator} corresponding to the given {@link
     * BinaryExpression.Operator}.
     */
    public static BooleanOperator valueOf(BinaryExpression.Operator op) {
        switch (op) {
        case EQUAL:
            return EQUAL;
        case NOT_EQUAL:
            return NOT_EQUAL;
        case ALTERNATIVE_NOT_EQUAL:
            return ALT_NOT_EQUAL;
        case EXACTLY_EQUAL:
            return EXACTLY_EQUAL;
        case EXACTLY_NOT_EQUAL:
            return EXACTLY_NOT_EQUAL;
        case LESS_THAN:
            return LESS_THAN;
        case GREATER_THAN:
            return GREATER_THAN;
        case LESS_THAN_OR_EQUAL:
            return LESS_THAN_OR_EQUAL;
        case GREATER_THAN_OR_EQUAL:
            return GREATER_THAN_OR_EQUAL;
        case LOGICAL_AND:
            return LOGICAL_AND;
        case LOGICAL_OR:
            return LOGICAL_OR;
        default:
            throw new IllegalArgumentException("Invalid boolean operator " + op);
        }
    }
}
