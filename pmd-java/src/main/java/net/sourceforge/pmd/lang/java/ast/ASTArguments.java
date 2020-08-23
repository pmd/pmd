/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;

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
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }
}
