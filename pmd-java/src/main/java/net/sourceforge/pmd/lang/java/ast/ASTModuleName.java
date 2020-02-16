/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTModuleName extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTModuleName(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTModuleName(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
