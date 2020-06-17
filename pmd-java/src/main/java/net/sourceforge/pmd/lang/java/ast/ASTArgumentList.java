/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTArgumentList extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTArgumentList(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the number of arguments.
     *
     * @return the number of arguments.
     */
    public int size() {
        return this.getNumChildren();
    }
}
