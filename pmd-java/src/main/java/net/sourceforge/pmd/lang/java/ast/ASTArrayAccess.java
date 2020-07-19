/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.InternalInterfaces.QualifierOwner;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An array access expression.
 *
 * <pre class="grammar">
 *
 * ArrayAccess ::=  {@link ASTExpression Expression} "["  {@link ASTExpression Expression} "]"
 *
 * </pre>
 */
public final class ASTArrayAccess extends AbstractJavaExpr implements ASTAssignableExpr, QualifierOwner {
    ASTArrayAccess(int id) {
        super(id);
    }


    /**
     * Returns the expression to the left of the "[".
     * This can never be a {@linkplain ASTTypeExpression type},
     * and is never {@linkplain ASTAmbiguousName ambiguous}.
     */
    @NonNull
    @Override
    public ASTExpression getQualifier() {
        return (ASTExpression) getChild(0);
    }

    /** Returns the expression within the brackets. */
    public ASTExpression getIndexExpression() {
        return (ASTExpression) getChild(1);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
