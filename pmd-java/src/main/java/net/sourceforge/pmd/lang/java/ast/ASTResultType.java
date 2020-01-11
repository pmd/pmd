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

    public boolean returnsArray() {
        return !isVoid() && ((ASTType) jjtGetChild(0)).isArray();
    }

    public boolean isVoid() {
        return jjtGetNumChildren() == 0;
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
