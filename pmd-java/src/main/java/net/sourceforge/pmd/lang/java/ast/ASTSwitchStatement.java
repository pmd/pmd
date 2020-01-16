/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a {@code switch} statement. See {@link ASTSwitchLike} for
 * its grammar.
 */
public final class ASTSwitchStatement extends AbstractStatement implements ASTSwitchLike {

    ASTSwitchStatement(int id) {
        super(id);
    }

    ASTSwitchStatement(JavaParser p, int id) {
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

}
