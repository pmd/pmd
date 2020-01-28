/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a type test on an object.
 *
 * <pre class="grammar">
 *
 * InstanceOfExpression ::= {@linkplain ASTExpression Expression} "instanceof" {@linkplain ASTTypeExpression TypeExpression}
 *
 * </pre>
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public class ASTInstanceOfExpression extends AbstractJavaExpr implements ASTExpression {

    ASTInstanceOfExpression(int id) {
        super(id);
    }


    public BinaryOp getOperator() {
        return BinaryOp.INSTANCEOF;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

    public ASTTypeExpression getRightOperand() {
        return (ASTTypeExpression) getChild(1);
    }

    /** Gets the wrapped type node. */
    public ASTType getTypeNode() {
        return getRightOperand().getTypeNode();
    }

}
