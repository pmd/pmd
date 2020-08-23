/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a type test on an object.
 *
 * <pre class="grammar">
 *
 * InstanceOfExpression ::= {@linkplain ASTExpression Expression} "instanceof" ({@linkplain ASTTypeExpression TypeExpression} | {@link ASTPattern Pattern})
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
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }



    public ASTTypeExpression getRightOperand() {
        return (ASTTypeExpression) getChild(1);
    }

    /**
     * Gets the type against which the expression is tested.
     */
    public ASTType getTypeNode() {
        return getRightOperand().getTypeNode();
    }

}
