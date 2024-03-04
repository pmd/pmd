/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.UnaryExpression;

/**
 * Apex prefix operator
 */
public enum PrefixOperator {
    POSITIVE("+"),
    NEGATIVE("-"),
    LOGICAL_NOT("!"),
    BITWISE_NOT("~"),
    INCREMENT("++"),
    DECREMENT("--");

    private final String symbol;

    PrefixOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    /**
     * Returns a {@link PrefixOperator} corresponding to the given {@link
     * UnaryExpression.Operator}.
     */
    public static PrefixOperator valueOf(UnaryExpression.Operator op) {
        switch (op) {
        case PLUS:
            return POSITIVE;
        case NEGATION:
            return NEGATIVE;
        case LOGICAL_COMPLEMENT:
            return LOGICAL_NOT;
        case BITWISE_NOT:
            return BITWISE_NOT;
        case PRE_INCREMENT:
            return INCREMENT;
        case PRE_DECREMENT:
            return DECREMENT;
        default:
            throw new IllegalArgumentException("Invalid prefix operator " + op);
        }
    }
}
