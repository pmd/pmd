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
 */
public final class ASTInstanceOfExpression extends ASTInfixExpression implements ASTExpression {

    ASTInstanceOfExpression(int id) {
        super(id);
    }


    ASTInstanceOfExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public BinaryOp getOperator() {
        return BinaryOp.INSTANCEOF;
    }

    @Override
    void setOp(BinaryOp op) {
        if (op != BinaryOp.INSTANCEOF) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

    @Override
    public ASTTypeExpression getRhs() {
        return (ASTTypeExpression) jjtGetChild(1);
    }

    /** Gets the wrapped type node. */
    public ASTType getTypeNode() {
        return getRhs().getTypeNode();
    }

    @Override
    public String getXPathNodeName() {
        // keep it uniform for XPath
        return "InfixExpression";
    }
}
