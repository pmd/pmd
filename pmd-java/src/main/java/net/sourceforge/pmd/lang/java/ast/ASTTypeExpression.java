/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Wraps a type node but presents the interface of {@link ASTExpression}.
 * This is used as the right-hand side of {@link ASTInstanceOfExpression instanceof expressions}.
 * TODO use as the LHS of eg field access and method calls
 *
 * <pre class="grammar">
 *
 * TypeExpression ::= {@link ASTType Type}
 *
 * </pre>
 */
public final class ASTTypeExpression extends AbstractJavaExpr implements ASTPrimaryExpression, JSingleChildNode<ASTType> {

    ASTTypeExpression(ASTType wrapped) {
        super(JavaParserTreeConstants.JJTTYPEEXPRESSION);
        this.jjtAddChild(wrapped, 0);
        copyTextCoordinates((AbstractJavaNode) wrapped);
    }


    ASTTypeExpression(JavaParser p, int id) {
        super(p, id);
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
    public ASTType jjtGetChild(int index) {
        return (ASTType) super.jjtGetChild(index);
    }

    /** Gets the wrapped type node. */
    public ASTType getTypeNode() {
        return jjtGetChild(0);
    }

}
