/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBooleanLiteral extends AbstractJavaTypeNode {

    private boolean isTrue;

    @InternalApi
    @Deprecated
    public ASTBooleanLiteral(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTBooleanLiteral(JavaParser p, int id) {
        super(p, id);
    }

    @InternalApi
    @Deprecated
    public void setTrue() {
        isTrue = true;
    }

    public boolean isTrue() {
        return this.isTrue;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
