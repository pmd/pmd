/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an assignment expression.
 *
 * <pre class="grammar">
 *
 * AssignmentExpression ::= {@link ASTAssignableExpr AssignableExpr} {@link AssignmentOp} {@link ASTExpression Expression}
 *
 * </pre>
 */
public final class ASTAssignmentExpression extends AbstractJavaExpr implements InternalInterfaces.BinaryExpressionLike {

    private AssignmentOp operator;


    ASTAssignmentExpression(int id) {
        super(id);
    }


    void setOp(AssignmentOp op) {
        this.operator = op;
    }

    /** Returns the left-hand side, ie the expression being assigned to. */
    @Override
    @NonNull
    public ASTAssignableExpr getLeftOperand() {
        return (ASTAssignableExpr) getChild(0);
    }


    /**
     * Returns whether this is a compound assignment (any operator except "=").
     */
    public boolean isCompound() {
        return operator.isCompound();
    }


    @Override
    @NonNull
    public AssignmentOp getOperator() {
        return operator;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
