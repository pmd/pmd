/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Node that may be qualified by an expression, e.g. an instance method call or
 * inner class constructor invocation.
 *
 * <pre class="grammar">
 *
 * QualifiableExpression ::= {@link ASTArrayAccess ArrayAccess}
 *                         | {@link ASTConstructorCall ConstructorCall}
 *                         | {@link ASTFieldAccess FieldAccess}
 *                         | {@link ASTMethodCall MethodCall}
 *                         | {@link ASTMethodReference MethodReference}
 *
 * </pre>
 */
public interface QualifiableExpression extends ASTPrimaryExpression {

    /**
     * Returns the expression to the left of the "." if it exists.
     * This may be a {@link ASTTypeExpression type expression}, or
     * an {@link ASTAmbiguousName ambiguous name}.
     */
    default @Nullable ASTExpression getQualifier() {
        return AstImplUtil.getChildAs(this, 0, ASTExpression.class);
    }
}
