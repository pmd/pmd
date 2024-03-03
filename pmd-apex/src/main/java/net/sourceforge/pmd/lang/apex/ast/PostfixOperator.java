/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.UnaryExpression;

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
     * Returns a {@link PostfixOperator} corresponding to the given {@link
     * UnaryExpression.Operator}.
     */
    public static PostfixOperator valueOf(UnaryExpression.Operator op) {
        switch (op) {
        case POST_INCREMENT:
            return INCREMENT;
        case POST_DECREMENT:
            return DECREMENT;
        default:
            throw new IllegalArgumentException("Invalid postfix operator " + op);
        }
    }
}
