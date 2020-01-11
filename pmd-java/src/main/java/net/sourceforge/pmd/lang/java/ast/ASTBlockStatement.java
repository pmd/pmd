/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBlockStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTBlockStatement(int id) {
        super(id);
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
     * Tells if this BlockStatement is an allocation statement. This is done by
     *
     * @return the result of
     *     containsDescendantOfType(ASTAllocationExpression.class)
     */
    public final boolean isAllocation() {
        return hasDescendantOfType(ASTAllocationExpression.class);
    }
}
