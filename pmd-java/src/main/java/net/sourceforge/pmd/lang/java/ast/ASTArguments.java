/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated Replaced by {@link ASTArgumentList}
 */
@Deprecated
public class ASTArguments extends AbstractJavaNode {

    ASTArguments(int id) {
        super(id);
    }

    public int getArgumentCount() {
        if (this.getNumChildren() == 0) {
            return 0;
        }
        return this.getChild(0).getNumChildren();
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
