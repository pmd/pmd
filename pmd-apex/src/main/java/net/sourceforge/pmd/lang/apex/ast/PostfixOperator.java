/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.PostfixOp;

/**
 * Apex postfix operator
 */
public enum PostfixOperator {
    INCREMENT("++"),
    DECREMENT("--");

    private final String symbol;

    PostfixOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    /**
     * Returns a {@link PostfixOperator} corresponding to the given {@link PostfixOp}.
     */
    public static PostfixOperator valueOf(PostfixOp op) {
        switch (op) {
        case INC:
            return INCREMENT;
        case DEC:
            return DECREMENT;
        default:
            throw new IllegalArgumentException("Invalid postfix operator " + op);
        }
    }
}
