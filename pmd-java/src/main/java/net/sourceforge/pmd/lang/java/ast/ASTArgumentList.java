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

    @InternalApi
    @Deprecated
    public ASTArgumentList(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
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
