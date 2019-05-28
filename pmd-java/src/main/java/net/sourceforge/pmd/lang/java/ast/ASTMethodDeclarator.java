/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTMethodDeclarator extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTMethodDeclarator(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTMethodDeclarator(JavaParser p, int id) {
        super(p, id);
    }

    public int getParameterCount() {
        return getFirstChildOfType(ASTFormalParameters.class).getParameterCount();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
