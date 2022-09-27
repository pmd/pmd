/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.PrefixOp;

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
     * Returns a {@link PrefixOperator} corresponding to the given {@link PrefixOp}.
     */
    public static PrefixOperator valueOf(PrefixOp op) {
        switch (op) {
        case POSITIVE:
            return POSITIVE;
        case NEGATIVE:
            return NEGATIVE;
        case NOT:
            return LOGICAL_NOT;
        case BITWISE_COMPLEMENT:
            return BITWISE_NOT;
        case INC:
            return INCREMENT;
        case DEC:
            return DECREMENT;
        default:
            throw new IllegalArgumentException("Invalid prefix operator " + op);
        }
    }
}
