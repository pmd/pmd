/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an assignment operator in an {@linkplain ASTExpression assignment expression}.
 *
 * <pre>
 *
 *  AssignmentOperator ::= "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | ">>>=" | "&=" | "^=" | "|="
 *
 * </pre>
 * @deprecated Superseded by {@link ASTAssignmentExpression}
 */
@Deprecated
public class ASTAssignmentOperator extends AbstractJavaNode {

    private boolean isCompound;

    ASTAssignmentOperator(int id) {
        super(id);
    }

    ASTAssignmentOperator(JavaParser p, int id) {
        super(p, id);
    }


    public boolean isCompound() {
        return this.isCompound;
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
