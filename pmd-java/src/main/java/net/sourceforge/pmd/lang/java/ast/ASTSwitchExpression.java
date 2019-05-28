/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTSwitchExpression extends AbstractJavaTypeNode {

    @Deprecated
    @InternalApi
    ASTSwitchExpression(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    ASTSwitchExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=8b1747ca53f66203ee212a3699a9a2f3 (do not edit this line) */
