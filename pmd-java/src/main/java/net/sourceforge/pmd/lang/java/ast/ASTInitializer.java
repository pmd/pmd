/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTInitializer extends AbstractJavaNode {

    private boolean isStatic;

    @InternalApi
    @Deprecated
    public ASTInitializer(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTInitializer(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isStatic() {
        return isStatic;
    }

    @InternalApi
    @Deprecated
    public void setStatic() {
        isStatic = true;
    }
}
