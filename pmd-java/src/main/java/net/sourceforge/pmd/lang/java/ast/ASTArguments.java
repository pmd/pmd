/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;

/**
 * @deprecated Replaced by {@link ASTArgumentList}
 */
@Deprecated
public class ASTArguments extends AbstractJavaNode {

    ASTArguments(int id) {
        super(id);
    }

    /**
     * Gets the number of arguments.
     * @return
     */
    public int size() {
        if (this.getNumChildren() == 0) {
            return 0;
        }
        return ((ASTArgumentList) this.getChild(0)).size();
    }

    /**
     * @deprecated for removal. Use {@link #size()} or {@link ASTArgumentList#size()} instead.
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@Size")
    public int getArgumentCount() {
        return size();
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
