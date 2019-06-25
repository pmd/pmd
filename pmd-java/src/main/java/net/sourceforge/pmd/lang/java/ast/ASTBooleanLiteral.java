/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The boolean literal, either "true" or "false".
 */
public final class ASTBooleanLiteral extends AbstractJavaExpr implements ASTLiteral {

    private boolean isTrue;


    ASTBooleanLiteral(int id) {
        super(id);
    }


    ASTBooleanLiteral(JavaParser p, int id) {
        super(p, id);
    }


    void setTrue() {
        isTrue = true;
    }

    public boolean isTrue() {
        return this.isTrue;
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
