/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated {@link ASTStatement} has been turned into an interface. Usages of BlockStatement can
 *  either be replaced with Statement if you don't care about the specific statement, or removed if
 *  you were extracting the contained node anyway.
 */
@Deprecated
public final class ASTBlockStatement extends AbstractJavaNode {

    ASTBlockStatement(int id) {
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
    public boolean isAllocation() {
        return hasDescendantOfType(ASTAllocationExpression.class);
    }
}
