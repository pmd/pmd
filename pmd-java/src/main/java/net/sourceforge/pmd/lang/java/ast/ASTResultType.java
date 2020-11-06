/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTResultType extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTResultType(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTResultType(JavaParser p, int id) {
        super(p, id);
    }

    public boolean returnsArray() {
        return !isVoid() && ((ASTType) getChild(0)).isArray();
    }

    public boolean isVoid() {
        return getNumChildren() == 0;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
