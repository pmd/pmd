/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public final class ASTFinallyStatement extends AbstractJavaNode {

    ASTFinallyStatement(int id) {
        super(id);
    }

    ASTFinallyStatement(JavaParser p, int id) {
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


    /**
     * Returns the body of this finally clause.
     */
    public ASTBlock getBody() {
        return (ASTBlock) jjtGetChild(0);
    }
}
