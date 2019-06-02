/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Node that may be qualified by an expression, e.g. an instance method call or
 * inner class constructor invocation.
 */
interface ASTQualifiableExpression extends ASTExpression {

    /**
     * Returns the expression to the left of the "." if it exists.
     * That may be an {@linkplain ASTAmbiguousName ambiguous name}.
     * May return null if this call is not qualified (no "."), or
     * if the qualifier is a type instead of an expression.
     */
    @Nullable
    default ASTPrimaryExpression getLhsExpression() {
        return AstImplUtil.getChildAs(this, 0, ASTPrimaryExpression.class);
    }
}
