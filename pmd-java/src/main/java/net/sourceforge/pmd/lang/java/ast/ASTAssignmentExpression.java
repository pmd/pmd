/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an assignment expression.
 *
 * <pre class="grammar">
 *
 * AssignmentExpression ::= {@link ASTAssignableExpr AssignableExpr} {@link AssignmentOp} {@link ASTExpression Expression}
 *
 * </pre>
 */
public final class ASTAssignmentExpression extends AbstractJavaExpr implements ASTExpression {

    private AssignmentOp operator;


    ASTAssignmentExpression(int id) {
        super(id);
    }


    ASTAssignmentExpression(JavaParser p, int id) {
        super(p, id);
    }


    void setOp(AssignmentOp op) {
        this.operator = op;
    }


    public ASTAssignableExpr getLeftHandSide() {
        return (ASTAssignableExpr) jjtGetChild(0);
    }


    public ASTExpression getRightHandSide() {
        return (ASTExpression) jjtGetChild(1);
    }


    /**
     * Returns whether this is a compound assignment (any operator except "=").
     */
    public boolean isCompound() {
        return operator.isCompound();
    }


    /**
     * Returns the assignment operator.
     */
    public AssignmentOp getOp() {
        return operator;
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
