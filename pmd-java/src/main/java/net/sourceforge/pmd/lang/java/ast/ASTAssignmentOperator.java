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
 */
public class ASTAssignmentOperator extends AbstractJavaNode {

    private boolean isCompound;

    @InternalApi
    @Deprecated
    public ASTAssignmentOperator(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTAssignmentOperator(JavaParser p, int id) {
        super(p, id);
    }

    // TODO this could be determined from the image of the operator, no need to set it in the parser...
    @InternalApi
    @Deprecated
    public void setCompound() {
        isCompound = true;
    }

    public boolean isCompound() {
        return this.isCompound;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
