/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Node that may be qualified by an expression, e.g. an instance method call or
 * inner class constructor invocation.
 */
// package private, just shares the method name, not sure if it's useful to publish it
interface ASTQualifiableExpression extends ASTExpression {

    /**
     * Returns the expression to the left of the "." if it exists.
     * This may be a {@link ASTTypeExpression type expression}, or
     * an {@link ASTAmbiguousName ambiguous name}.
     */
    @Nullable
    default ASTPrimaryExpression getQualifier() {
        return AstImplUtil.getChildAs(this, 0, ASTPrimaryExpression.class);
    }
}
